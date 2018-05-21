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
package com.android.tools.idea.editors.hprof;

import com.android.SdkConstants;
import com.android.tools.idea.flags.StudioFlags;
import com.android.tools.idea.profiling.capture.FileCaptureType;
import com.google.wireless.android.sdk.stats.AndroidStudioEvent.ProfilerCaptureType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import icons.AndroidIcons;
import org.jetbrains.annotations.NotNull;

public class HprofCaptureType extends FileCaptureType {
  protected HprofCaptureType() {
    super("Heap Snapshot", AndroidIcons.Ddms.DumpHprof, SdkConstants.DOT_HPROF);
  }

  @NotNull
  @Override
  public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
    return new HprofEditor(project, file);
  }

  @Override
  public ProfilerCaptureType getCaptureType() {
    return ProfilerCaptureType.HEAP;
  }

  @Override
  public boolean accept(@NotNull VirtualFile file) {
    // If flag is enabled, .hprof files will be accepted by AndroidProfilerCaptureEditorProvider instead of CaptureEditorProvider.
    return !StudioFlags.PROFILER_OPEN_CAPTURES.get();
  }
}
