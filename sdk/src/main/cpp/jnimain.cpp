// Copyright eeGeo Ltd (2012-2015), All Rights Reserved

#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <pthread.h>
#include <Android/Input/AndroidInput.h>
#include <Android/AndroidNativeState.h>
#include <Android/Input/TouchEventWrapper.h>
#include <Android/AndroidImageNameHelper.h>
#include "jnimain.h"
//#include "Logger.h"
#include "AndroidAppThreadAssertions.h"
#include "AndroidApiRunner.h"
#include "SdkJni.h"
#include "EegeoApi.h"
#include "EegeoCameraApi.h"
#include "AndroidInputProcessor.h"
#include "AndroidSdkInputHandler.h"

namespace
{
    // todo TouchInputEvent is poorly formed, but can't hide public fields without breaking consumption by apps
    Eegeo::Android::Input::TouchInputEvent MakeTouchInputEvent(
        JNIEnv* jenv,
        Eegeo::Android::Input::TouchEvent::Type touchEventType,
        jint primaryActionIndex,
        jint primaryActionIdentifier,
        jint numPointers,
        jfloatArray x,
        jfloatArray y,
        jintArray pointerIdentity,
        jintArray pointerIndex
    )
    {
        jfloat* xBuffer = jenv->GetFloatArrayElements(x, 0);
        jfloat* yBuffer = jenv->GetFloatArrayElements(y, 0);
        jint* identityBuffer = jenv->GetIntArrayElements(pointerIdentity, 0);
        jint* indexBuffer = jenv->GetIntArrayElements(pointerIndex, 0);

        std::vector<Eegeo::Android::Input::TouchInputPointerEvent> pointerEvents;
        pointerEvents.reserve(numPointers);
        for(int i = 0; i < numPointers; ++ i)
        {
            pointerEvents.emplace_back(xBuffer[i], yBuffer[i], identityBuffer[i], indexBuffer[i]);
        }

        Eegeo::Android::Input::TouchInputEvent event(touchEventType, primaryActionIndex, primaryActionIdentifier, pointerEvents);

        jenv->ReleaseFloatArrayElements(x, xBuffer, 0);
        jenv->ReleaseFloatArrayElements(y, yBuffer, 0);
        jenv->ReleaseIntArrayElements(pointerIdentity, identityBuffer, 0);
        jenv->ReleaseIntArrayElements(pointerIndex, indexBuffer, 0);
        return event;
    }
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* pvt)
{
    Eegeo_ASSERT(vm != nullptr);
    Eegeo::ApiHost::Android::AndroidAppThreadAssertions::NominateCurrentlyRunningThreadAsUiThread();
    Eegeo::ApiHost::Android::registerJniVM(vm);
    return JNI_VERSION_1_6;
}

//lifecycle
JNIEXPORT jlong JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeCreateApiRunner(
		JNIEnv* jenv, jobject obj,
        jobject nativeMapView,
		jobject context,
		jobject assetManager,
		jfloat dpi,
        jint density,
        jstring versionName,
        jint versionCode)
{
    Eegeo::ApiHost::Android::AndroidAppThreadAssertions::NominateCurrentlyRunningThreadAsNativeThread();

    auto assetManagerGlobalRef = jenv->NewGlobalRef(assetManager);
    auto assetManagerFromJava = AAssetManager_fromJava(jenv, assetManagerGlobalRef);

    auto nativeMapViewRef = jenv->NewGlobalRef(nativeMapView);
    auto nativeMapViewClass = (jclass)jenv->NewGlobalRef(jenv->GetObjectClass(nativeMapViewRef));

    auto contextGlobalRef = jenv->NewGlobalRef(context);
    auto contextClass = (jclass)jenv->NewGlobalRef(jenv->GetObjectClass(contextGlobalRef));

    jmethodID getClassLoader = jenv->GetMethodID(contextClass,"getClassLoader", "()Ljava/lang/ClassLoader;");
    auto classLoaderGlobalRef = jenv->NewGlobalRef(jenv->CallObjectMethod(context, getClassLoader));
    auto classLoaderClassGlobalRef  = (jclass)jenv->NewGlobalRef(jenv->FindClass("java/lang/ClassLoader"));
    auto classLoaderLoadClassMethod = jenv->GetMethodID(classLoaderClassGlobalRef, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");

    // DetermineDeviceModel
    jclass buildClass = jenv->FindClass("android/os/Build");
    jfieldID modelMethod = jenv->GetStaticFieldID(buildClass, "MODEL", "Ljava/lang/String;");
    jstring deviceNameString = (jstring)jenv->GetStaticObjectField(buildClass, modelMethod);

    const char* pDeviceName = jenv->GetStringUTFChars(deviceNameString, NULL);
    const char* pVersionName = jenv->GetStringUTFChars(versionName, NULL);

    Eegeo::Android::Sdk::NativeState* pNativeState = Eegeo_NEW(Eegeo::Android::Sdk::NativeState)(
        Eegeo::ApiHost::Android::g_javaVM,
        jenv,
        nativeMapViewRef,
        nativeMapViewClass,
        assetManagerGlobalRef,
        assetManagerFromJava,
        // todo - NativeState has assumption that context is and Activity, needs further fixup of dependencies in platform
        contextGlobalRef,
        contextClass,
        classLoaderGlobalRef,
        classLoaderClassGlobalRef,
        classLoaderLoadClassMethod,
        dpi,
        std::string(pDeviceName),
        std::string(pVersionName),
        static_cast<int>(versionCode)
    );

    jenv->ReleaseStringUTFChars(deviceNameString, pDeviceName);
    jenv->ReleaseStringUTFChars(versionName, pVersionName);

    jthrowable exc;
    exc = jenv->ExceptionOccurred();

    if (exc)
    {
        jenv->ExceptionDescribe();
        jenv->ExceptionClear();
        return 0;
    }


    AndroidNativeState legacyAndroidNativeState;
    pNativeState->ToLegacyAndroidNativeState(legacyAndroidNativeState);
    Eegeo::Android::AndroidImageNameHelper imageHelper(&legacyAndroidNativeState);

    Eegeo::Android::Sdk::IAndroidGlDisplayService* pGlDisplayService = Eegeo_NEW(Eegeo::Android::Sdk::AndroidGlDisplayService)(
        *pNativeState,
        dpi,
        imageHelper.GetImageResolutionScale(),
        imageHelper.GetImageResolutionSuffix()
    );

    auto pAndroidInputHandler = Eegeo_NEW(Eegeo::Android::Sdk::AndroidSdkInputHandler)();

    const float dummyScreenDimension = 1.f;
    auto pAndroidInputProcessor = Eegeo_NEW(Eegeo::Android::Input::AndroidInputProcessor)(pAndroidInputHandler, dummyScreenDimension, dummyScreenDimension);

    auto pAndroidApiRunner = Eegeo_NEW(Eegeo::ApiHost::Android::AndroidApiRunner)(pNativeState, pGlDisplayService, pAndroidInputProcessor, pAndroidInputHandler);

    return ((long)pAndroidApiRunner);
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeDestroyApiRunner(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr)
{
//    EXAMPLE_LOG("stopNativeCode()\n");

    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    Eegeo_DELETE pAndroidApiRunner;

    Eegeo::ApiHost::Android::AndroidAppThreadAssertions::RemoveNominationForNativeThread();
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativePauseApiRunner(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr)
{
    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    pAndroidApiRunner->Pause();
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeResumeApiRunner(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr)
{
    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    pAndroidApiRunner->Resume();
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeSetSurface(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr, jobject surface)
{
    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    Eegeo::Android::Sdk::NativeState& nativeState = pAndroidApiRunner->GetNativeState();

    if (surface != NULL)
    {
        ANativeWindow* window = ANativeWindow_fromSurface(jenv, surface);

        if (window != NULL)
        {
            pAndroidApiRunner->ActivateSurface(window);
        }
    }
}

JNIEXPORT jlong JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeCreateEegeoMapApi(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr, jstring apiKey, jobject eegeoMap)
{
    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    const char* pApiKey = jenv->GetStringUTFChars( apiKey, NULL ) ;

    pAndroidApiRunner->CreatePlatform(std::string(pApiKey), eegeoMap);
    Eegeo::ApiHost::IEegeoApiHostModule* pEegeoApiHostModule = pAndroidApiRunner->GetEegeoApiHostModule();

    Eegeo::Api::EegeoMapApi* pEegeoMapApi = &(pEegeoApiHostModule->GetEegeoMapApi());

    jenv->ReleaseStringUTFChars(apiKey, pApiKey);

    return reinterpret_cast<jlong>(pEegeoMapApi);
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeUpdateApiRunner(JNIEnv* jenv,
                                                                              jobject obj,
                                                                              jlong jniApiRunnerPtr,
                                                                              jfloat deltaSeconds)
{
    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    pAndroidApiRunner->UpdateNative((float)deltaSeconds);
}




JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeProcessPointerDown(JNIEnv* jenv, jobject obj,
    jlong jniApiRunnerPtr,
        jint primaryActionIndex,
        jint primaryActionIdentifier,
        jint numPointers,
        jfloatArray x,
        jfloatArray y,
        jintArray pointerIdentity,
        jintArray pointerIndex)
{
    const Eegeo::Android::Input::TouchInputEvent& event = MakeTouchInputEvent(
        jenv, Eegeo::Android::Input::TouchEvent::PointerDown, primaryActionIndex, primaryActionIdentifier, numPointers, x, y, pointerIdentity, pointerIndex);

    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    pAndroidApiRunner->HandleTouchEvent(event);
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeProcessPointerUp(JNIEnv* jenv, jobject obj,
                                                                                          jlong jniApiRunnerPtr,
        jint primaryActionIndex,
        jint primaryActionIdentifier,
        jint numPointers,
        jfloatArray x,
        jfloatArray y,
        jintArray pointerIdentity,
        jintArray pointerIndex)
{
    const Eegeo::Android::Input::TouchInputEvent& event = MakeTouchInputEvent(
        jenv, Eegeo::Android::Input::TouchEvent::PointerUp, primaryActionIndex, primaryActionIdentifier, numPointers, x, y, pointerIdentity, pointerIndex);

    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    pAndroidApiRunner->HandleTouchEvent(event);
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeProcessPointerMove(JNIEnv* jenv, jobject obj,
                                                                                            jlong jniApiRunnerPtr,
        jint primaryActionIndex,
        jint primaryActionIdentifier,
        jint numPointers,
        jfloatArray x,
        jfloatArray y,
        jintArray pointerIdentity,
        jintArray pointerIndex)
{
    const Eegeo::Android::Input::TouchInputEvent& event = MakeTouchInputEvent(
        jenv, Eegeo::Android::Input::TouchEvent::PointerMove, primaryActionIndex, primaryActionIdentifier, numPointers, x, y, pointerIdentity, pointerIndex);

    Eegeo_ASSERT(jniApiRunnerPtr != 0);
    auto pAndroidApiRunner = reinterpret_cast<Eegeo::ApiHost::Android::AndroidApiRunner*>(jniApiRunnerPtr);

    pAndroidApiRunner->HandleTouchEvent(event);
}

JNIEXPORT void JNICALL Java_com_eegeo_mapapi_NativeJniCalls_setView(JNIEnv*, jobject obj, jlong jniEegeoMapApiPtr,
                                                                      jboolean animated, jdouble latDegrees, jdouble lonDegrees, jdouble altitude, jboolean modifyPosition,
                                                                      jdouble distance, jboolean modifyDistance,
                                                                      jdouble headingDegrees, jboolean modifyHeading,
                                                                      jdouble tiltDegrees, jboolean modifyTilt,
                                                                      jdouble transitionDurationSeconds, jboolean hasTransitionDuration,
                                                                      jboolean jumpIfFarAway,
                                                                      jboolean allowInterruption)
{
    Eegeo_ASSERT(jniEegeoMapApiPtr != 0);
    auto pEegeoMapApi = reinterpret_cast<Eegeo::Api::EegeoMapApi*>(jniEegeoMapApiPtr);

    pEegeoMapApi->GetCameraApi().SetView(animated, latDegrees, lonDegrees, altitude, modifyPosition,
                                         distance, modifyDistance,
                                         headingDegrees, modifyHeading,
                                         tiltDegrees, modifyTilt,
                                         transitionDurationSeconds, hasTransitionDuration,
                                         jumpIfFarAway,
                                         allowInterruption);
}

extern "C"
{
    void eegeoMapInitializedCallback(int mapId, Eegeo::Api::EegeoMapApi* mapApi)
    {
        Eegeo_TTY("Received eegeoMapInitializedCallback, mapId = %d", mapId);
    }
}