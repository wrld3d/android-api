package com.eegeo.mapapi.camera;

import android.support.annotation.WorkerThread;

public class CameraApiJniCalls {
    @WorkerThread
    public static native void setView(long jniEegeoMapApiPtr, boolean animated, double latDegrees, double longDegrees, double altitude, boolean modifyPosition,
                                      double distance, boolean modifyDistance,
                                      double headingDegrees, boolean modifyHeading,
                                      double tiltDegrees, boolean modifyTilt,
                                      double transitionDurationSeconds, boolean hasTransitionDuration,
                                      boolean jumpIfFarAway,
                                      boolean allowInterruption);


    @WorkerThread
    public static native void setViewToBounds(long jniEegeoMapApiPtr, boolean animated, double northEastLat, double northEastLon, double southWestLat, double southWestLon, boolean allowInterruption);

    @WorkerThread
    public static native void tryStopTransition(long jniEegeoMapApiPtr);

    @WorkerThread
    public static native void initView(long jniEegeoMapApiPtr,
                                       double latDegrees,
                                       double longDegrees,
                                       double altitude,
                                       double distance,
                                       double headingDegrees,
                                       double tiltDegrees,
                                       boolean modifyTilt);

    @WorkerThread
    public static native void moveCameraIdentityCameraPositionUpdate(long jniEegeoMapApiPtr, CameraPosition cameraPostion);
}