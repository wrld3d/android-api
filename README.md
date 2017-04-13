# WRLD Android Java Api

A library for displaying beautiful, interactive 3D maps on Android devices. 

This repository contains source code for the Java library, intended for developers contributing to the library itself.

If you want to use the library in your Android application, then see our [documentation](https://docs.wrld3d.com/android/latest/docs/api/) for guidance on how to add WRLD maps to your Android app. Our [samples github repository](https://github.com/wrld3d/android-api-samples) contains example apps to help get you started.

The library is available as a Maven package on [Bintray](https://bintray.com/wrld/maven/android-sdk).

[ ![Download](https://api.bintray.com/packages/wrld/maven/android-sdk/images/download.svg) ](https://bintray.com/wrld/maven/android-sdk/_latestVersion)


## Status
This library is currently in alpha, and is undergoing active development. We plan to add further features in the near future. Got something you want to do in your app? Let us know via the [issues](https://github.com/wrld3d/android-api/issues) page.

## Building the library with Android Studio

### Requirements

* [Android Studio](https://developer.android.com/studio/index.html), together with the Android SDK, NDK and supporting libraries.
* Prebuilt C++ libraries and headers for the WRLD C++ SDK.  Download the latest by running ```./update.platform.sh```.
* For running on Android devices, the library requires Android 4.0.3 (API level 15) or higher.

### Setup

In Android Studio, select "Open..." and then select the top level android-api folder.  If Android Studio is missing supporting libraries or components, you may be prompted to install these. 

You should then see two modules in the project: [sdk](https://github.com/wrld3d/android-api/tree/master/sdk) and [sdkdoclet](https://github.com/wrld3d/android-api/tree/master/sdkdoclet).


### Build

The ```assemble``` task will create an Android archive (.aar) file suitable for use in an application.  To speed up build times, you can reduce the number of build platforms by editing the list of ABI filters in the sdk's [build.gradle](https://github.com/wrld3d/android-api/blob/master/sdk/build.gradle) file.


## Further information
See our [api samples](https://github.com/wrld3d/android-api-samples) for complete applications using the SDK, and our [documentation](https://docs.wrld3d.com/android/latest/docs/api/) for additional information.

Questions, comments, or problems? All feedback is welcome -- just [create an issue](https://github.com/wrld3d/android-api/issues).

## License
The Android SDK is released under the Simplified BSD License. See the [LICENSE.md](https://github.com/wrld3d/android-api/blob/master/LICENSE.md) file for details.
