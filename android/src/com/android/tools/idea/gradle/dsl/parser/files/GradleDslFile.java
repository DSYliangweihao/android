/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.android.tools.idea.gradle.dsl.parser.files;

import com.android.tools.idea.gradle.dsl.api.BuildModelNotification;
import com.android.tools.idea.gradle.dsl.parser.BuildModelContext;
import com.android.tools.idea.gradle.dsl.parser.GradleDslParser;
import com.android.tools.idea.gradle.dsl.parser.GradleDslWriter;
import com.android.tools.idea.gradle.dsl.parser.apply.ApplyDslElement;
import com.android.tools.idea.gradle.dsl.parser.elements.GradleNameElement;
import com.android.tools.idea.gradle.dsl.parser.elements.GradlePropertiesDslElement;
import com.android.tools.idea.gradle.dsl.parser.groovy.GroovyDslParser;
import com.android.tools.idea.gradle.dsl.parser.groovy.GroovyDslWriter;
import com.google.common.collect.Sets;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.intellij.openapi.vfs.VfsUtilCore.virtualToIoFile;

/**
 * Provides Gradle specific abstraction over a {@link GroovyFile}.
 */
public abstract class GradleDslFile extends GradlePropertiesDslElement {
  @NotNull private final VirtualFile myFile;
  @NotNull private final Project myProject;
  @NotNull private final Set<GradleDslFile> myChildModuleDslFiles = Sets.newHashSet();
  @NotNull private final GradleDslWriter myGradleDslWriter;
  @NotNull private final GradleDslParser myGradleDslParser;

  @Nullable private GradleDslFile myParentModuleDslFile;
  @Nullable private GradleDslFile mySiblingDslFile;

  @NotNull private final List<ApplyDslElement> myAppliedFiles;
  @NotNull private final BuildModelContext myBuildModelContext;

  protected GradleDslFile(@NotNull VirtualFile file,
                          @NotNull Project project,
                          @NotNull String moduleName,
                          @NotNull BuildModelContext context) {
    super(null, null, GradleNameElement.fake(moduleName));
    myFile = file;
    myProject = project;
    myAppliedFiles = new ArrayList<>();
    myBuildModelContext = context;

    Application application = ApplicationManager.getApplication();
    PsiFile psiFile = application.runReadAction((Computable<PsiFile>)() -> PsiManager.getInstance(myProject).findFile(myFile));

    // Pick the language that should be used by this GradleDslFile, we do this by selecting the parser implementation.
    GroovyFile groovyPsiFile;
    if (psiFile instanceof GroovyFile) {
      groovyPsiFile = (GroovyFile)psiFile;
      myGradleDslParser = new GroovyDslParser(groovyPsiFile, this);
      myGradleDslWriter = new GroovyDslWriter();
    }
    else {
      // If we don't support the language we ignore the PsiElement and set stubs for the writer and parser.
      // This means this file will produce an empty model.
      myGradleDslParser = new GradleDslParser.Adapter();
      myGradleDslWriter = new GradleDslWriter.Adapter();
      return;
    }

    setPsiElement(groovyPsiFile);
  }

  /**
   * Parses the gradle file again. This is a convenience method when an already parsed gradle file needs to be parsed again
   * (for example, after making changes to the PSI elements.)
   */
  public void reparse() {
    clear();
    parse();
  }

  public void parse() {
    myGradleDslParser.parse();
    // Apply all of the files.
    mergeAppliedFiles();
  }

  @NotNull
  public Project getProject() {
    return myProject;
  }

  @NotNull
  public VirtualFile getFile() {
    return myFile;
  }

  @NotNull
  public File getDirectoryPath() {
    return virtualToIoFile(getFile().getParent());
  }

  @NotNull
  public List<GradleDslFile> getAppliedFiles() {
    return myAppliedFiles.stream().map(ApplyDslElement::getAppliedDslFile).collect(Collectors.toList());
  }

  public void setParentModuleDslFile(@NotNull GradleDslFile parentModuleDslFile) {
    myParentModuleDslFile = parentModuleDslFile;
    myParentModuleDslFile.myChildModuleDslFiles.add(this);
  }

  @Nullable
  public GradleDslFile getParentModuleDslFile() {
    return myParentModuleDslFile;
  }

  @NotNull
  public Collection<GradleDslFile> getChildModuleDslFiles() {
    return myChildModuleDslFiles;
  }

  /**
   * Sets the sibling dsl file of this file.
   *
   * <p>build.gradle and gradle.properties files belongs to the same module are considered as sibling files.
   */
  public void setSiblingDslFile(@NotNull GradleDslFile siblingDslFile) {
    mySiblingDslFile = siblingDslFile;
  }

  /**
   * Returns the sibling dsl file of this file.
   *
   * <p>build.gradle and gradle.properties files belongs to the same module are considered as sibling files.
   */
  @Nullable
  public GradleDslFile getSiblingDslFile() {
    return mySiblingDslFile;
  }

  @NotNull
  public GradleDslWriter getWriter() {
    return myGradleDslWriter;
  }

  @NotNull
  public GradleDslParser getParser() {
    return myGradleDslParser;
  }

  @NotNull
  public BuildModelContext getContext() {
    return myBuildModelContext;
  }

  @Override
  protected void apply() {
    // First make sure we update all our applied files.
    for (ApplyDslElement applyElement : myAppliedFiles) {
      if (applyElement.getAppliedDslFile() != null) {
        applyElement.getAppliedDslFile().apply();
      }
    }

    // And update us.
    super.apply();
  }

  public void registerAppliedFile(@NotNull ApplyDslElement applyElement) {
    myAppliedFiles.add(applyElement);
  }

  // TODO: Fix cycle here.
  private void mergeAppliedFiles() {
    for (ApplyDslElement applyElement : myAppliedFiles) {
      VirtualFile file = applyElement.getAppliedFile();
      // Don't apply if the file is null.
      if (file == null) {
        continue;
      }

      // Parse the file
      GradleDslFile dslFile = myBuildModelContext.getOrCreateBuildFile(file);
      applyElement.setAppliedDslFile(dslFile);

      addAppliedModelProperties(dslFile);
    }

    // Make sure any dependencies are correct.
    getContext().getDependencyManager().resolveAll();
  }

  @NotNull
  public List<BuildModelNotification> getPublicNotifications() {
    return myBuildModelContext.getPublicNotifications();
  }
}
