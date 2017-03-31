# android-sdk

[ ![Download](https://api.bintray.com/packages/eegeo/maven/android-sdk/images/download.svg) ](https://bintray.com/eegeo/maven/android-sdk/_latestVersion)

Welcome to the eeGeo Android SDK, a Java library for eegeo maps on Android devices. 

This repository contains source code for building the Java library from the eeGeo C++ SDK, and is intended
for developers working on the library itself.
If you just want to use the library in your Android application, see our [documentation](https://docs.eegeo.com/android/latest/docs/api/) and [examples](https://github.com/eegeo/android-sdk-example) for instructions.

Requirements
------------
* [Android Studio](https://developer.android.com/studio/index.html), together with the Android SDK, NDK and supporting libraries.
* Prebuilt C++ libraries and headers for the eeGeo C++ SDK.  Download the latest by running ```./update.platform.sh```.

Getting started
---------------

In Android Studio, select "Open..." and then select the top level android-sdk folder.  If Android Studio is missing supporting libraries or components, you may be prompted to install these. 

You should then see two modules in the project: [sdk](https://github.com/eegeo/android-sdk/tree/master/sdk) and [sdkdoclet](https://github.com/eegeo/android-sdk/tree/master/sdk).


Building the SDK
----------------

The ```assemble``` task will create an Android archive (.aar) file suitable for use in an application.  To speed up build times, you can reduce the number of build platforms by editing the list of ABI filters in the sdk's [build.gradle](https://github.com/eegeo/android-sdk/blob/master/sdk/build.gradle) file.

Building the docs
-----------------

The ```assemble``` task will build standard javadocs, but we also have a [sdkdoclet](https://github.com/eegeo/android-sdk/tree/master/sdkdoclet) module for generating markdown as an input to the documentation website build process.  The gradle task for building this documentation is ```apidoc```; see [the documentation repo](https://github.com/eegeo/android-sdk-docs) for full instructions.
 

Further information
-------------------
See our [example code](https://github.com/eegeo/android-sdk-example) for complete applications using the SDK, and our [documentation](https://github.com/eegeo/android-sdk-example) for additional information.

Questions, comments, or problems? All feedback is welcome -- just [create an issue](https://github.com/eegeo/android-sdk/issues).

License
-------

The Android SDK is released under the Simplified BSD License. See the [LICENSE.md](https://github.com/eegeo/android-sdk/blob/master/LICENSE.md) file for details.
