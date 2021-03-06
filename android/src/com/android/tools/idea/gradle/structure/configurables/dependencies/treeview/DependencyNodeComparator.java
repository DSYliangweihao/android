/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.tools.idea.gradle.structure.configurables.dependencies.treeview;

import com.android.tools.idea.gradle.structure.configurables.ui.dependencies.PsDependencyComparator;
import com.android.tools.idea.gradle.structure.model.PsBaseDependency;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class DependencyNodeComparator implements Comparator<AbstractDependencyNode> {
  @NotNull private final PsDependencyComparator myDependencyComparator;

  public DependencyNodeComparator(@NotNull PsDependencyComparator comparator) {
    myDependencyComparator = comparator;
  }

  @Override
  public int compare(AbstractDependencyNode n1, AbstractDependencyNode n2) {
    PsBaseDependency d1 = (PsBaseDependency)n1.getFirstModel();
    PsBaseDependency d2 = (PsBaseDependency)n2.getFirstModel();
    return myDependencyComparator.compare(d1, d2);
  }
}
