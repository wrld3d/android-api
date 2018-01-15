// Copyright eeGeo Ltd (2012-2015), All Rights Reserved

#pragma once

#include <jni.h>

extern "C"
{
    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* pvt);

    // Runner calls:
    JNIEXPORT jlong JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeCreateApiRunner(
    		JNIEnv* jenv, jobject obj,
            jobject nativeMapView,
    		jobject context,
    		jobject assetManager,
    		jfloat dpi,
    		jint density,
            jstring versionName,
            jint versionCode);

    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeDestroyApiRunner(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr);
    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativePauseApiRunner(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr);
    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeResumeApiRunner(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr);
    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeSetSurface(JNIEnv* jenv, jobject obj, jlong jniApiRunnerPtr, jobject surface);

    JNIEXPORT jlong JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeCreateEegeoMapApi(
        JNIEnv* jenv,
        jobject obj,
        jlong jniApiRunnerPtr,
        jobject eegeoMap,
        jstring apiKey,
        jstring coverageTreeManifest,
        jstring environmentThemesManifest
    );

    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeUpdateApiRunner(JNIEnv* jenv,
                                                                                  jobject obj,
                                                                                  jlong jniApiRunnerPtr,
                                                                                  jfloat deltaSeconds);


    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeProcessPointerDown(JNIEnv* jenv, jobject obj, jlong jniMapViewPtr,
            jint primaryActionIndex,
            jint primaryActionIdentifier,
            jint numPointers,
            jfloatArray x,
            jfloatArray y,
            jintArray pointerIdentity,
            jintArray pointerIndex);

    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeProcessPointerUp(JNIEnv* jenv, jobject obj, jlong jniMapViewPtr,
            jint primaryActionIndex,
            jint primaryActionIdentifier,
            jint numPointers,
            jfloatArray x,
            jfloatArray y,
            jintArray pointerIdentity,
            jintArray pointerIndex);

    JNIEXPORT void JNICALL Java_com_eegeo_mapapi_EegeoNativeMapView_nativeProcessPointerMove(JNIEnv* jenv, jobject obj, jlong jniMapViewPtr,
            jint primaryActionIndex,
            jint primaryActionIdentifier,
            jint numPointers,
            jfloatArray x,
            jfloatArray y,
            jintArray pointerIdentity,
            jintArray pointerIndex);
};

