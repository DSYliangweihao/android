// Copyright (C) 2017 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.android.tools.idea.gradle.structure.model.android

import com.android.builder.model.ProductFlavor
import com.android.tools.idea.gradle.dsl.api.android.ProductFlavorModel
import com.android.tools.idea.gradle.structure.model.PsChildModel
import com.android.tools.idea.gradle.structure.model.helpers.*
import com.android.tools.idea.gradle.structure.model.meta.*
import com.google.common.util.concurrent.Futures.immediateFuture
import java.io.File

data class PsProductFlavorKey(val dimension: String, val name: String)

open class PsProductFlavor(
  final override val parent: PsAndroidModule
) : PsChildModel() {
  override val descriptor by ProductFlavorDescriptors
  var resolvedModel: ProductFlavor? = null
  private var parsedModel: ProductFlavorModel? = null

  constructor(parent: PsAndroidModule, resolvedModel: ProductFlavor?, parsedModel: ProductFlavorModel?) : this(parent) {
    init(resolvedModel, parsedModel)
  }

  fun init(resolvedModel: ProductFlavor?, parsedModel: ProductFlavorModel?) {
    this.resolvedModel = resolvedModel
    this.parsedModel = parsedModel
  }

  override val name: String get() = resolvedModel?.name ?: parsedModel?.name() ?: ""

  var applicationId by ProductFlavorDescriptors.applicationId
  var applicationIdSuffix by ProductFlavorDescriptors.applicationIdSuffix
  var dimension by ProductFlavorDescriptors.dimension
  var maxSdkVersion by ProductFlavorDescriptors.maxSdkVersion
  var minSdkVersion by ProductFlavorDescriptors.minSdkVersion
  var multiDexEnabled by ProductFlavorDescriptors.multiDexEnabled
  var targetSdkVersion by ProductFlavorDescriptors.targetSdkVersion
  var testApplicationId by ProductFlavorDescriptors.testApplicationId
  var testFunctionalTest by ProductFlavorDescriptors.testFunctionalTest
  var testHandleProfiling by ProductFlavorDescriptors.testHandleProfiling
  var testInstrumentationRunner by ProductFlavorDescriptors.testInstrumentationRunner
  var versionCode by ProductFlavorDescriptors.versionCode
  var versionName by ProductFlavorDescriptors.versionName
  var versionNameSuffix by ProductFlavorDescriptors.versionNameSuffix
  var resConfigs by ProductFlavorDescriptors.resConfigs
  var manifestPlaceholders by ProductFlavorDescriptors.manifestPlaceholders
  var testInstrumentationRunnerArguments by ProductFlavorDescriptors.testInstrumentationRunnerArguments

  override val isDeclared: Boolean get() = parsedModel != null

  object ProductFlavorDescriptors : ModelDescriptor<PsProductFlavor, ProductFlavor, ProductFlavorModel> {
    override fun getResolved(model: PsProductFlavor): ProductFlavor? = model.resolvedModel

    override fun getParsed(model: PsProductFlavor): ProductFlavorModel? = model.parsedModel

    override fun setModified(model: PsProductFlavor) {
      model.isModified = true
    }

    val applicationId: SimpleProperty<PsProductFlavor, String> = property(
      "Application ID",
      resolvedValueGetter = { applicationId },
      parsedPropertyGetter = { applicationId() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    val applicationIdSuffix: SimpleProperty<PsProductFlavor, String> = property(
      "Application Id Suffix",
      resolvedValueGetter = { applicationIdSuffix },
      parsedPropertyGetter = { applicationIdSuffix() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    val dimension: SimpleProperty<PsProductFlavor, String> = property(
      "Dimension",
      resolvedValueGetter = { dimension },
      parsedPropertyGetter = { dimension() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString,
      knownValuesGetter = { model -> immediateFuture(model.parent.flavorDimensions.map { ValueDescriptor(it.name, it.name) }) }
    )

    val maxSdkVersion: SimpleProperty<PsProductFlavor, Int> = property(
      "Max SDK Version",
      resolvedValueGetter = { maxSdkVersion },
      parsedPropertyGetter = { maxSdkVersion() },
      getter = { asInt() },
      setter = { setValue(it) },
      parser = ::parseInt,
      knownValuesGetter = ::installedSdksAsInts
    )

    val minSdkVersion: SimpleProperty<PsProductFlavor, String> = property(
      "Min SDK Version",
      resolvedValueGetter = { minSdkVersion?.apiLevel?.toString() },
      parsedPropertyGetter = { minSdkVersion() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString,
      knownValuesGetter = ::installedSdksAsStrings
    )

    val multiDexEnabled: SimpleProperty<PsProductFlavor, Boolean> = property(
      "Multi Dex Enabled",
      resolvedValueGetter = { multiDexEnabled },
      parsedPropertyGetter = { multiDexEnabled() },
      getter = { asBoolean() },
      setter = { setValue(it) },
      parser = ::parseBoolean,
      knownValuesGetter = ::booleanValues
    )

    val signingConfig: SimpleProperty<PsProductFlavor, Unit> = property(
      "Signing Config",
      resolvedValueGetter = { null },
      parsedPropertyGetter = { signingConfig() },
      getter = { asUnit() },
      setter = {},
      parser = ::parseReferenceOnly,
      formatter = ::formatUnit,
      knownValuesGetter = { model -> signingConfigs(model.parent) }
    )

    val targetSdkVersion: SimpleProperty<PsProductFlavor, String> = property(
      "Target SDK Version",
      resolvedValueGetter = { targetSdkVersion?.apiLevel?.toString() },
      parsedPropertyGetter = { targetSdkVersion() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString,
      knownValuesGetter = ::installedSdksAsStrings

    )

    val testApplicationId: SimpleProperty<PsProductFlavor, String> = property(
      "Test Application ID",
      resolvedValueGetter = { testApplicationId },
      parsedPropertyGetter = { testApplicationId() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    val testFunctionalTest: SimpleProperty<PsProductFlavor, Boolean> = property(
      "Test Functional Test",
      // TODO(b/111630584): Replace with the resolved value.
      resolvedValueGetter = { null },
      parsedPropertyGetter = { testFunctionalTest() },
      getter = { asBoolean() },
      setter = { setValue(it) },
      parser = ::parseBoolean,
      knownValuesGetter = ::booleanValues
    )

    val testHandleProfiling: SimpleProperty<PsProductFlavor, Boolean> = property(
      "Test Handle Profiling",
      // TODO(b/111630584): Replace with the resolved value.
      resolvedValueGetter = { null },
      parsedPropertyGetter = { testHandleProfiling() },
      getter = { asBoolean() },
      setter = { setValue(it) },
      parser = ::parseBoolean,
      knownValuesGetter = ::booleanValues
    )

    val testInstrumentationRunner: SimpleProperty<PsProductFlavor, String> = property(
      "Test instrumentation runner class name",
      resolvedValueGetter = { testInstrumentationRunner },
      parsedPropertyGetter = { testInstrumentationRunner() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    val versionCode: SimpleProperty<PsProductFlavor, Int> = property(
      "Version Code",
      resolvedValueGetter = { versionCode },
      parsedPropertyGetter = { versionCode() },
      getter = { asInt() },
      setter = { setValue(it) },
      parser = ::parseInt
    )

    val versionName: SimpleProperty<PsProductFlavor, String> = property(
      "Version Name",
      resolvedValueGetter = { versionName },
      parsedPropertyGetter = { versionName() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    val versionNameSuffix: SimpleProperty<PsProductFlavor, String> = property(
      "Version Name Suffix",
      resolvedValueGetter = { versionNameSuffix },
      parsedPropertyGetter = { versionNameSuffix() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    val matchingFallbacks: ListProperty<PsProductFlavor, String> = listProperty(
      "Matching Fallbacks",
      resolvedValueGetter = { null },
      parsedPropertyGetter = { matchingFallbacks() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString,
      variableMatchingStrategy = VariableMatchingStrategy.WELL_KNOWN_VALUE,
      knownValuesGetter = { model -> productFlavorMatchingFallbackValues(model.parent.parent, model.dimension.maybeValue) }
    )

    val consumerProGuardFiles: ListProperty<PsProductFlavor, File> = listProperty(
      "Consumer ProGuard Files",
      resolvedValueGetter = { consumerProguardFiles.toList() },
      parsedPropertyGetter = { consumerProguardFiles() },
      getter = { asFile() },
      setter = { setValue(it.toString()) },
      parser = ::parseFile,
      knownValuesGetter = { model -> proGuardFileValues(model.parent) }
    )

    val proGuardFiles: ListProperty<PsProductFlavor, File> = listProperty(
      "ProGuard Files",
      resolvedValueGetter = { proguardFiles.toList() },
      parsedPropertyGetter = { proguardFiles() },
      getter = { asFile() },
      setter = { setValue(it.toString()) },
      parser = ::parseFile,
      knownValuesGetter = { model -> proGuardFileValues(model.parent) }
    )

    val resConfigs: ListProperty<PsProductFlavor, String> = listProperty(
      "Resource Configs",
      resolvedValueGetter = { resourceConfigurations.toList() },
      parsedPropertyGetter = { resConfigs() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    val manifestPlaceholders: MapProperty<PsProductFlavor, Any> = mapProperty(
      "Manifest Placeholders",
      resolvedValueGetter = { manifestPlaceholders },
      parsedPropertyGetter = { manifestPlaceholders() },
      getter = { asAny() },
      setter = { setValue(it) },
      parser = ::parseAny
    )

    val testInstrumentationRunnerArguments: MapProperty<PsProductFlavor, String> = mapProperty(
      "Test Instrumentation Runner Arguments",
      resolvedValueGetter = { testInstrumentationRunnerArguments },
      parsedPropertyGetter = { testInstrumentationRunnerArguments() },
      getter = { asString() },
      setter = { setValue(it) },
      parser = ::parseString
    )

    override val properties: Collection<ModelProperty<PsProductFlavor, *, *, *>> =
      listOf(applicationId, applicationIdSuffix, dimension, maxSdkVersion, minSdkVersion, multiDexEnabled, signingConfig, targetSdkVersion,
             testApplicationId, testFunctionalTest, testHandleProfiling, testInstrumentationRunner, versionCode, versionName,
             versionNameSuffix, matchingFallbacks, consumerProGuardFiles, proGuardFiles, resConfigs, manifestPlaceholders,
             testInstrumentationRunnerArguments)
  }
}
