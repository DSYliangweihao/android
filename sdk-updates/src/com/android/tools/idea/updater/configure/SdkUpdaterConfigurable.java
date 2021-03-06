/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.updater.configure;

import com.android.repository.api.*;
import com.android.repository.impl.meta.Archive;
import com.android.repository.impl.meta.RepositoryPackages;
import com.android.repository.util.InstallerUtil;
import com.android.sdklib.devices.Storage;
import com.android.sdklib.repository.AndroidSdkHandler;
import com.android.sdklib.repository.meta.DetailsTypes;
import com.android.tools.idea.help.StudioHelpManagerImpl;
import com.android.tools.idea.sdk.StudioDownloader;
import com.android.tools.idea.sdk.StudioSettingsController;
import com.android.tools.idea.sdk.progress.StudioLoggerProgressIndicator;
import com.android.tools.idea.sdk.wizard.SdkQuickfixUtils;
import com.android.tools.idea.wizard.model.ModelWizardDialog;
import com.android.utils.FileUtils;
import com.android.utils.HtmlBuilder;
import com.android.utils.Pair;
import com.google.common.base.Objects;
import com.google.common.collect.*;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.io.File;
import java.util.*;

/**
 * Configurable for the Android SDK Manager.
 * TODO(jbakermalone): implement the searchable interface more completely.
 */
public class SdkUpdaterConfigurable implements SearchableConfigurable {
  /**
   * Very rough zip decompression estimate for informational/UI purposes only. Better for it to be a bit higher than average,
   * but not too much. Most of the SDK component files are binary, which should yield 2x-3x compression rate
   * on average - at least this is the assumption we are making here.
   *
   * TODO: The need for this will disappear should we revise the packages XML schema and add installation size for a given
   * platform there.
   */
  private static final int ESTIMATED_ZIP_DECOMPRESSION_RATE = 4;

  private SdkUpdaterConfigPanel myPanel;
  private Channel myCurrentChannel;
  private Runnable myChannelChangedCallback;

  @NotNull
  @Override
  public String getId() {
    return "AndroidSdkUpdater";
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Android SDK Updater";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return StudioHelpManagerImpl.STUDIO_HELP_PREFIX + "r/studio-ui/sdk-manager.html";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    myChannelChangedCallback = () -> {
      Channel channel = StudioSettingsController.getInstance().getChannel();
      if (myCurrentChannel == null) {
        myCurrentChannel = channel;
      }
      if (!Objects.equal(channel, myCurrentChannel)) {
        myCurrentChannel = channel;
        myPanel.refresh(true);
      }
    };
    myPanel =
      new SdkUpdaterConfigPanel(myChannelChangedCallback, new StudioDownloader(), StudioSettingsController.getInstance(), this);
    JComponent component = myPanel.getComponent();
    component.addAncestorListener(new AncestorListenerAdapter() {
      @Override
      public void ancestorAdded(AncestorEvent event) {
        myChannelChangedCallback.run();
      }
    });

    return myPanel.getComponent();
  }

  /**
   * Gets the {@link AndroidSdkHandler} to use. Note that the instance can change if the local sdk path is edited, and so should not be
   * cached.
   */
  AndroidSdkHandler getSdkHandler() {
    return AndroidSdkHandler.getInstance(myPanel.getSelectedSdkLocation());
  }

  RepoManager getRepoManager() {
    return getSdkHandler().getSdkManager(new StudioLoggerProgressIndicator(getClass()));
  }

  @Override
  public boolean isModified() {
    if (myPanel.isModified()) {
      return true;
    }

    // If the user modifies the channel, comes back here, and then applies the change, we want to be able to update
    // right away. Thus we mark ourselves as modified if UpdateSettingsConfigurable is modified, and then reload in
    // apply().
    DataContext dataContext = DataManager.getInstance().getDataContext(myPanel.getComponent());
    Settings data = Settings.KEY.getData(dataContext);
    if (data != null) {
      Configurable updatesConfigurable = data.find("preferences.updates");
      if (updatesConfigurable != null) {
        return updatesConfigurable.isModified();
      }
    }
    return false;
  }

  @Override
  public void apply() throws ConfigurationException {
    boolean sourcesModified = myPanel.areSourcesModified();
    myPanel.saveSources();

    final List<LocalPackage> toDelete = Lists.newArrayList();
    final Map<RemotePackage, UpdatablePackage> requestedPackages = Maps.newHashMap();
    for (PackageNodeModel model : myPanel.getStates()) {
      if (model.getState() == PackageNodeModel.SelectedState.NOT_INSTALLED) {
        if (model.getPkg().hasLocal()) {
          toDelete.add(model.getPkg().getLocal());
        }
      }
      else if (model.getState() == PackageNodeModel.SelectedState.INSTALLED &&
               (model.getPkg().isUpdate() || !model.getPkg().hasLocal())) {
        UpdatablePackage pkg = model.getPkg();
        requestedPackages.put(pkg.getRemote(), pkg);
      }
    }
    boolean found = false;
    long spaceToBeFreedUp = 0;
    long patchesDownloadSize = 0, fullInstallationsDownloadSize = 0;
    HtmlBuilder messageToDelete = new HtmlBuilder();
    if (!toDelete.isEmpty()) {
      found = true;
      messageToDelete.add("The following components will be deleted: \n");
      messageToDelete.beginList();

      try {
        spaceToBeFreedUp = ProgressManager.getInstance().runProcessWithProgressSynchronously(
          () -> getLocalInstallationSize(toDelete),
          "Gathering Package Information", true, null);
      }
      catch (ProcessCanceledException e) {
        throw new ConfigurationException("Installation was canceled.");
      }
      for (LocalPackage item : toDelete) {
        messageToDelete.listItem()
                       .add(item.getDisplayName()).add(", Revision: ")
                       .add(item.getVersion().toString());
      }
      messageToDelete.endList();
    }
    HtmlBuilder messageToInstall = new HtmlBuilder();
    if (!requestedPackages.isEmpty()) {
      found = true;
      messageToInstall.add("The following components will be installed: \n");
      messageToInstall.beginList();
      Multimap<RemotePackage, RemotePackage> dependencies = HashMultimap.create();
      ProgressIndicator progress = new StudioLoggerProgressIndicator(getClass());
      RepositoryPackages packages = getRepoManager().getPackages();
      for (RemotePackage item : requestedPackages.keySet()) {
        List<RemotePackage> packageDependencies = InstallerUtil.computeRequiredPackages(ImmutableList.of(item), packages, progress);
        if (packageDependencies == null) {
          Messages.showErrorDialog((Project)null, "Unable to resolve dependencies for " + item.getDisplayName(), "Dependency Error");
          throw new ConfigurationException("Unable to resolve dependencies.");
        }
        for (RemotePackage dependency : packageDependencies) {
          dependencies.put(dependency, item);
        }
        messageToInstall.listItem().add(String.format("%1$s %2$s %3$s", item.getDisplayName(),
                                             item.getTypeDetails() instanceof DetailsTypes.ApiDetailsType ? "revision" : "version",
                                             item.getVersion()));

        Pair<Long, Boolean> itemDownloadSize = calculateDownloadSizeForPackage(item, packages);
        if (itemDownloadSize.getSecond()) {
          patchesDownloadSize += itemDownloadSize.getFirst();
        }
        else {
          fullInstallationsDownloadSize += itemDownloadSize.getFirst();
        }
      }
      for (RemotePackage dependency : dependencies.keySet()) {
        if (requestedPackages.containsKey(dependency)) {
          continue;
        }
        Set<RemotePackage> requests = Sets.newHashSet(dependencies.get(dependency));
        requests.remove(dependency);
        if (!requests.isEmpty()) {
          messageToInstall.listItem().add(dependency.getDisplayName())
            .add(" (Required by ");
          Iterator<RemotePackage> requestIterator = requests.iterator();
          messageToInstall.add(requestIterator.next().getDisplayName());
          while (requestIterator.hasNext()) {
            messageToInstall.add(", ").add(requestIterator.next().getDisplayName());
          }
          messageToInstall.add(")");
          Pair<Long, Boolean> itemDownloadSize = calculateDownloadSizeForPackage(dependency, packages);
          if (itemDownloadSize.getSecond()) {
            patchesDownloadSize += itemDownloadSize.getFirst();
          }
          else {
            fullInstallationsDownloadSize += itemDownloadSize.getFirst();
          }
        }
      }
      messageToInstall.endList();
    }

    if (found) {
      Pair<HtmlBuilder, HtmlBuilder> diskUsageMessages = getDiskUsageMessages(fullInstallationsDownloadSize, patchesDownloadSize,
                                                                              spaceToBeFreedUp);
      // Now form the summary message ordering the constituents properly.
      HtmlBuilder message = new HtmlBuilder();
      message.openHtmlBody();
      if (diskUsageMessages.getSecond() != null) {
        message.addHtml(diskUsageMessages.getSecond().getHtml());
      }
      message.addHtml(messageToDelete.getHtml());
      message.addHtml(messageToInstall.getHtml());
      message.addHtml(diskUsageMessages.getFirst().getHtml());
      message.closeHtmlBody();
      if (confirmChange(message)) {
        if (!requestedPackages.isEmpty() || !toDelete.isEmpty()) {
          ModelWizardDialog dialog =
            SdkQuickfixUtils.createDialogForPackages(myPanel.getComponent(), requestedPackages.values(), toDelete, true);
          if (dialog != null) {
            dialog.show();
            for (RemotePackage remotePackage : requestedPackages.keySet()) {
              PackageOperation installer = getRepoManager().getInProgressInstallOperation(remotePackage);
              if (installer != null) {
                PackageOperation.StatusChangeListener listener = (installer1, progress) -> myPanel.getComponent().repaint();
                installer.registerStateChangeListener(listener);
              }
            }
          }
        }

        myPanel.refresh(sourcesModified);
      }
      else {
        throw new ConfigurationException("Installation was canceled.");
      }
    }
    else {
      // We didn't have any changes, so just reload (maybe the channel changed).
      myChannelChangedCallback.run();
    }
  }

  private static long getLocalInstallationSize(@NotNull Collection<LocalPackage> localPackages) {
    long size = 0;
    for (LocalPackage item : localPackages) {
      if (item != null) {
        // TODO: Consider adding installation size to the package manifest.
        for (File f : FileUtils.getAllFiles(item.getLocation())) {
          size += f.length();
        }
      }
    }
    return size;
  }

  /**
   * Attempts to calculate the download size based on package's archive metadata.
   *
   * @param remotePackage the package to calculate the download size for.
   * @param packages loaded repository packages obtained from the SDK handler.
   * @return A pair of long and boolean, where the first element denotes the calculated size,
   * and the second indicates whether it's a patch installation.
   */
  private static Pair<Long, Boolean> calculateDownloadSizeForPackage(@NotNull RemotePackage remotePackage,
                                                                     @NotNull RepositoryPackages packages) {
    LocalPackage localPackage = packages.getLocalPackages().get(remotePackage.getPath());
    Archive archive = remotePackage.getArchive();
    if (archive == null) {
      // There is not much we can do in this case, but it should "never be reached".
      return Pair.of(0L, false);
    }
    if (localPackage != null) {
      Archive.PatchType patch = archive.getPatch(localPackage.getVersion());
      if (patch != null) {
        return Pair.of(patch.getSize(), true);
      }
    }
    return Pair.of(archive.getComplete().getSize(), false);
  }

  private Pair<HtmlBuilder, HtmlBuilder> getDiskUsageMessages(long fullInstallationsDownloadSize,
                                                              long patchesDownloadSize, long spaceToBeFreedUp) {
    HtmlBuilder message = new HtmlBuilder();
    message.add("Disk usage:\n");
    boolean issueDiskSpaceWarning = false;
    message.beginList();
    if (spaceToBeFreedUp > 0) {
      message.listItem().add("Disk space that will be freed: " + new Storage(spaceToBeFreedUp).toUiString());
    }
    long totalDownloadSize = patchesDownloadSize + fullInstallationsDownloadSize;
    if (totalDownloadSize > 0) {
      message.listItem().add("Estimated download size: " + new Storage(totalDownloadSize).toUiString());
      long tempDirUsageAfterDownload = patchesDownloadSize + ESTIMATED_ZIP_DECOMPRESSION_RATE * fullInstallationsDownloadSize;
      message.listItem().add("Estimated disk space required in temp directory during installation: "
                             + new Storage(tempDirUsageAfterDownload).toUiString());
      long sdkRootUsageAfterInstallation = ESTIMATED_ZIP_DECOMPRESSION_RATE * fullInstallationsDownloadSize;
      message.listItem().add("Estimated disk space to be additionally occupied on SDK partition after installation: "
                             + new Storage(sdkRootUsageAfterInstallation).toUiString());
      File tempDir = new File(System.getProperty("java.io.tmpdir"));
      File sdkRoot = getSdkHandler().getLocation();
      long sdkRootUsableSpace = 0;
      if (sdkRoot != null) {
        sdkRootUsableSpace = sdkRoot.getUsableSpace();
      }
      long tempDirUsableSpace = tempDir.getUsableSpace();
      // Checking for strict equality between the available space does not 100% guarantee that the SDK root and temp
      // folders are on the same partition of course, but for the purposes of this dialog the frequency of that edge case can be
      // considered negligible. Even when it happens, the dialog will still show technically correct information, just not as
      // fully as in the regular case.
      // Also there is an opposite edge case when there were some changes to the disk space between the two calls to
      // getUsableSpace() above. The probability of that can be considered negligible in this context as well, and even
      // when it happens, it'll still be fine.
      if (sdkRoot == null || sdkRootUsableSpace == tempDirUsableSpace) {
        message.listItem().add("Currently available disk space: " + new Storage(tempDirUsableSpace).toUiString());
      }
      else {
        message.listItem().add(String.format("Currently available disk space in SDK root (%1$s): %2$s", sdkRoot,
                                             new Storage(sdkRootUsableSpace).toUiString()));
        message.listItem().add(String.format("Currently available disk space in tmpdir (%1$s): %2$s", tempDir,
                                             new Storage(tempDirUsableSpace).toUiString()));
      }
      long totalSdkUsableSpace = sdkRootUsableSpace + spaceToBeFreedUp;
      issueDiskSpaceWarning = ((tempDirUsableSpace < tempDirUsageAfterDownload)
                               || ((sdkRoot != null) && (totalSdkUsableSpace < sdkRootUsageAfterInstallation)));
    }
    message.endList();
    if (issueDiskSpaceWarning) {
      HtmlBuilder warningMessage = new HtmlBuilder();
      warningMessage.beginColor(JBColor.RED)
                    .addBold("WARNING: There might be insufficient disk space to perform this operation. ")
                    .newline().newline()
                    .add("Estimated disk usage is presented below. ")
                    .add("Consider freeing up more disk space before proceeding. ")
                    .endColor()
                    .newline().newline();
      return Pair.of(message, warningMessage);
    }
    return Pair.of(message, null);
  }

  private static boolean confirmChange(HtmlBuilder message) {
    String[] options = {Messages.OK_BUTTON, Messages.CANCEL_BUTTON};
    Icon icon = AllIcons.General.Warning;

    // I would use showOkCancelDialog but Mac sheet panels do not gracefully handle long messages and their buttons can display offscreen
    return Messages.showIdeaMessageDialog(null, message.getHtml(),
                                          "Confirm Change", options, 0, icon, null) == Messages.OK;
  }

  @Override
  public void reset() {
    myPanel.reset();
  }

  @Override
  public void disposeUIResources() {
    if (myPanel != null)
      Disposer.dispose(myPanel);
  }
}
