load("//tools/base/bazel:bazel.bzl", "iml_module")

# managed by go/iml_to_build
iml_module(
    name = "intellij.android.kotlin.output.parser",
    srcs = ["idea-android/idea-android-output-parser/src"],
    iml_files = ["idea-android/idea-android-output-parser/intellij.android.kotlin.output.parser.iml"],
    visibility = ["//visibility:public"],
    # do not sort: must match IML order
    runtime_deps = [
        "//tools/idea/xml/relaxng:intellij.relaxng",
        "//tools/idea/platform/tasks-platform-impl:intellij.platform.tasks.impl",
        "//tools/idea/json:intellij.json",
        "//tools/idea/platform/credential-store:intellij.platform.credentialStore",
        "//tools/idea/jvm/jvm-analysis-impl:intellij.jvm.analysis.impl",
        "//tools/idea/java/compiler/instrumentation-util-8:intellij.java.compiler.instrumentationUtil.java8",
        "//tools/idea/plugins/maven/error-prone-compiler:intellij.maven.errorProne.compiler",
        "//tools/idea/plugins/IntelliLang/intellilang-jps-plugin:intellij.java.langInjection.jps",
        "//tools/adt/idea/adt-branding:intellij.android.adt.branding",
    ],
    # do not sort: must match IML order
    deps = [
        "//tools/idea/.idea/libraries:KotlinJavaRuntime",
        "//tools/adt/idea/android:intellij.android.core[module]",
        "//tools/idea/.idea/libraries:kotlin-plugin",
    ],
)

# managed by go/iml_to_build
iml_module(
    name = "intellij.android.kotlin.idea",
    srcs = ["idea-android/src"],
    iml_files = ["idea-android/intellij.android.kotlin.idea.iml"],
    visibility = ["//visibility:public"],
    exports = ["//tools/idea/.idea/libraries:kotlin-plugin"],
    # do not sort: must match IML order
    runtime_deps = [
        "//tools/idea/xml/relaxng:intellij.relaxng",
        "//tools/idea/platform/tasks-platform-impl:intellij.platform.tasks.impl",
        "//tools/idea/json:intellij.json",
        "//tools/idea/platform/credential-store:intellij.platform.credentialStore",
        "//tools/adt/idea/adt-branding:intellij.android.adt.branding",
        "//tools/idea/jvm/jvm-analysis-impl:intellij.jvm.analysis.impl",
        "//tools/idea/java/compiler/instrumentation-util-8:intellij.java.compiler.instrumentationUtil.java8",
        "//tools/idea/plugins/maven/error-prone-compiler:intellij.maven.errorProne.compiler",
        "//tools/idea/plugins/IntelliLang/intellilang-jps-plugin:intellij.java.langInjection.jps",
    ],
    # do not sort: must match IML order
    deps = [
        "//tools/adt/idea/android-common:intellij.android.common[module]",
        "//tools/adt/idea/android:intellij.android.core[module]",
        "//tools/idea/platform/analysis-api:intellij.platform.analysis[module]",
        "//tools/idea/platform/core-api:intellij.platform.core[module]",
        "//tools/idea/platform/editor-ui-api:intellij.platform.editor[module]",
        "//tools/idea/platform/external-system-api:intellij.platform.externalSystem[module]",
        "//tools/idea/platform/platform-api:intellij.platform.ide[module]",
        "//tools/idea:intellij.java.impl[module]",
        "//tools/idea/xml/dom-openapi:intellij.xml.dom[module]",
        "//tools/idea/xml/xml-psi-api:intellij.xml.psi[module]",
        "//tools/idea/.idea/libraries:KotlinJavaRuntime",
        "//tools/idea/.idea/libraries:Gradle",
        "//tools/idea/xml/xml-psi-impl:intellij.xml.psi.impl[module]",
        "//tools/idea/.idea/libraries:kotlin-plugin",
        "//tools/adt/idea/android-kotlin:lib/dx",
        "//tools/idea/plugins/gradle:intellij.gradle.common[module]",
    ],
)

# managed by go/iml_to_build
iml_module(
    name = "intellij.android.kotlin.extensions",
    srcs = ["android-extensions-idea/src"],
    iml_files = ["android-extensions-idea/intellij.android.kotlin.extensions.iml"],
    visibility = ["//visibility:public"],
    # do not sort: must match IML order
    runtime_deps = [
        "//tools/idea/xml/relaxng:intellij.relaxng",
        "//tools/idea/platform/tasks-platform-impl:intellij.platform.tasks.impl",
        "//tools/idea/json:intellij.json",
        "//tools/idea/platform/credential-store:intellij.platform.credentialStore",
        "//tools/idea/jvm/jvm-analysis-impl:intellij.jvm.analysis.impl",
        "//tools/idea/java/compiler/instrumentation-util-8:intellij.java.compiler.instrumentationUtil.java8",
        "//tools/idea/plugins/maven/error-prone-compiler:intellij.maven.errorProne.compiler",
        "//tools/idea/plugins/IntelliLang/intellilang-jps-plugin:intellij.java.langInjection.jps",
        "//tools/adt/idea/adt-branding:intellij.android.adt.branding",
    ],
    # do not sort: must match IML order
    deps = [
        "//tools/idea/platform/core-api:intellij.platform.core[module]",
        "//tools/adt/idea/android:intellij.android.core[module]",
        "//tools/idea/platform/core-impl:intellij.platform.core.impl[module]",
        "//tools/idea/platform/indexing-api:intellij.platform.indexing[module]",
        "//tools/idea/platform/projectModel-api:intellij.platform.projectModel[module]",
        "//tools/idea/platform/external-system-api:intellij.platform.externalSystem[module]",
        "//tools/idea/xml/xml-psi-api:intellij.xml.psi[module]",
        "//tools/idea/xml/xml-psi-impl:intellij.xml.psi.impl[module]",
        "//tools/idea/plugins/gradle:intellij.gradle[module]",
        "//tools/idea/platform/editor-ui-api:intellij.platform.editor[module]",
        "//tools/idea/platform/lang-impl:intellij.platform.lang.impl[module]",
        "//tools/idea/xml/dom-openapi:intellij.xml.dom[module]",
        "//tools/adt/idea/android-kotlin:intellij.android.kotlin.idea[module]",
        "//tools/analytics-library/crash:analytics-crash[module]",
        "//tools/idea/.idea/libraries:Gradle",
        "//tools/idea/.idea/libraries:KotlinJavaRuntime",
        "//tools/idea/.idea/libraries:kotlin-plugin",
    ],
)

# managed by go/iml_to_build
java_import(
    name = "lib/dx",
    jars = ["lib/dx.jar"],
    visibility = ["//visibility:public"],
)

# managed by go/iml_to_build
iml_module(
    name = "intellij.android.kotlin.extensions.jps",
    srcs = ["android-extensions-jps/src"],
    iml_files = ["android-extensions-jps/intellij.android.kotlin.extensions.jps.iml"],
    test_runtime_deps = ["//tools/idea/platform/external-system-impl:intellij.platform.externalSystem.impl"],
    visibility = ["//visibility:public"],
    # do not sort: must match IML order
    runtime_deps = [
        "//tools/idea/platform/usageView:intellij.platform.usageView",
        "//tools/idea/platform/lvcs-impl:intellij.platform.lvcs.impl",
        "//tools/idea/xml/xml-structure-view-impl:intellij.xml.structureView.impl",
        "//tools/idea/xml/dom-impl:intellij.xml.dom.impl",
        "//tools/idea/spellchecker:intellij.spellchecker",
        "//tools/idea/xml/relaxng:intellij.relaxng",
        "//tools/idea/platform/tasks-platform-impl:intellij.platform.tasks.impl",
        "//tools/idea/json:intellij.json",
        "//tools/idea/platform/credential-store:intellij.platform.credentialStore",
        "//tools/idea/platform/configuration-store-impl:intellij.platform.configurationStore.impl",
        "//tools/idea/platform/built-in-server:intellij.platform.builtInServer.impl",
        "//tools/adt/idea/adt-branding:intellij.android.adt.branding",
        "//tools/idea/jvm/jvm-analysis-impl:intellij.jvm.analysis.impl",
        "//tools/idea/java/compiler/instrumentation-util-8:intellij.java.compiler.instrumentationUtil.java8",
    ],
    # do not sort: must match IML order
    deps = [
        "//tools/adt/idea/jps-plugin:intellij.android.jps[module]",
        "//tools/idea/platform/core-api:intellij.platform.core[module]",
        "//tools/idea:intellij.platform.jps.build_and_others[module]",
        "//tools/idea/jps/model-api:intellij.platform.jps.model[module]",
        "//tools/idea/jps/model-serialization:intellij.platform.jps.model.serialization[module]",
        "//tools/idea/.idea/libraries:KotlinJavaRuntime",
        "//tools/idea/.idea/libraries:kotlin-plugin",
    ],
)

# managed by go/iml_to_build
iml_module(
    name = "intellij.android.kotlin.tests",
    iml_files = ["intellij.android.kotlin.tests.iml"],
    test_class = "org.jetbrains.kotlin.android.KotlinAndroidTestSuite",
    test_data = [
        "//prebuilts/studio/sdk:platforms/latest",
        "//prebuilts/tools/common/kotlin-plugin:kotlin-plugin-for-studio",
        "//tools/adt/idea/android/annotations",
        "//tools/idea:platform-build-info",
        ":testData",
    ],
    # do not sort: must match IML order
    test_runtime_deps = [
        "//tools/idea/platform/tasks-platform-impl:intellij.platform.tasks.impl",
        "//tools/idea/platform/credential-store:intellij.platform.credentialStore",
        "//tools/idea/java/compiler/instrumentation-util-8:intellij.java.compiler.instrumentationUtil.java8",
        "//tools/idea/jvm/jvm-analysis-impl:intellij.jvm.analysis.impl",
        "//tools/idea/plugins/maven/error-prone-compiler:intellij.maven.errorProne.compiler",
        "//tools/idea/plugins/IntelliLang/intellilang-jps-plugin:intellij.java.langInjection.jps",
        "//tools/adt/idea/adt-branding:intellij.android.adt.branding",
        "//tools/vendor/intellij/cidr:intellij.c.doxygen",
    ],
    test_srcs = [
        "android-extensions-idea/tests",
        "idea-android/tests",
    ],
    test_tags = ["manual"],
    visibility = ["//visibility:public"],
    # do not sort: must match IML order
    deps = [
        "//tools/adt/idea/android:intellij.android.core[module, test]",
        "//tools/adt/idea/android-kotlin:intellij.android.kotlin.extensions[module, test]",
        "//tools/adt/idea/android-kotlin:intellij.android.kotlin.idea[module, test]",
        "//tools/adt/idea/android-plugin:intellij.android.plugin[module, test]",
        "//tools/idea/.idea/libraries:kotlin-plugin-test[test]",
        "//tools/adt/idea/android-test-framework:intellij.android.testFramework[module, test]",
        "//tools/idea:intellij.platform.jps.build_and_others[module, test]",
        "//tools/idea/platform/testFramework:intellij.platform.testFramework[module, test]",
        "//tools/base/common:studio.android.sdktools.common[module, test]",
        "//tools/base/testutils:studio.android.sdktools.testutils[module, test]",
        "//tools/idea/.idea/libraries:gson[test]",
        "//tools/adt/idea/adt-testutils:intellij.android.adt.testutils[module, test]",
        "//tools/idea/xml/dom-openapi:intellij.xml.dom[module, test]",
    ],
)

filegroup(
    name = "testData",
    srcs = glob([
        "idea-android/testData/**",
        "android-extensions-idea/testData/**",
        "android-extensions-jps/testData/**",
        "android-extensions-runtime/src/**",
    ]),
    visibility = ["//visibility:public"],
)
