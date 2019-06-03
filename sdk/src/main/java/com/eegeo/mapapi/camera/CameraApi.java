package com.eegeo.mapapi.camera;


import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLngBounds;

/**
 * @eegeo.internal
 */
public class CameraApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;

    public CameraApi(INativeMessageRunner nativeRunner,
                        IUiMessageRunner uiRunner,
                        long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @WorkerThread
    public void moveCamera(@NonNull final CameraUpdate update) {
        switch (update.getUpdateType()) {
            case CameraPosition:
                performMoveCamera((CameraUpdateFactory.IdentityCameraPositionUpdate) update);
                break;
            case LatLngBounds:
                performMoveCamera((CameraUpdateFactory.LatLongBoundsCameraPositionUpdate) update);
                break;
        }
    }

    @WorkerThread
    public void cancelAnimation() {
        nativeCancelAnimation(m_jniEegeoMapApiPtr);
    }


    @WorkerThread
    public void animateCamera(@NonNull final CameraUpdate update, final CameraAnimationOptions animationOptions) {
        switch (update.getUpdateType()) {
            case CameraPosition:
                performAnimateCamera((CameraUpdateFactory.IdentityCameraPositionUpdate) update, animationOptions);
                break;
            case LatLngBounds:
                performAnimateCamera((CameraUpdateFactory.LatLongBoundsCameraPositionUpdate) update, animationOptions);
                break;
        }
    }

    @WorkerThread
    public void setIndoorCameraRestriction(final Boolean indoorCameraRestriction) {
        nativeSetIndoorCameraRestriction(m_jniEegeoMapApiPtr, indoorCameraRestriction);
    }

    @WorkerThread
    private void performMoveCamera(@NonNull final CameraUpdateFactory.IdentityCameraPositionUpdate cameraPositionUpdate) {
        final CameraPosition cameraPosition = cameraPositionUpdate.getCameraPosition();
        nativeMoveCameraIdentityCameraPositionUpdate(m_jniEegeoMapApiPtr, cameraPosition);
    }

    @WorkerThread
    private void performMoveCamera(@NonNull final CameraUpdateFactory.LatLongBoundsCameraPositionUpdate latLongBoundsUpdate) {
        final LatLngBounds latLngBounds = latLongBoundsUpdate.getLatLngBounds();
        nativeMoveCameraLatLongBoundsCameraPositionUpdate(m_jniEegeoMapApiPtr, latLngBounds);
    }

    @WorkerThread
    private void performAnimateCamera(@NonNull final CameraUpdateFactory.IdentityCameraPositionUpdate cameraPositionUpdate, final CameraAnimationOptions animationOptions) {
        final CameraPosition cameraPosition = cameraPositionUpdate.getCameraPosition();
        nativeAnimateCameraIdentityCameraPositionUpdate(m_jniEegeoMapApiPtr, cameraPosition, animationOptions);
    }

    @WorkerThread
    private void performAnimateCamera(@NonNull final CameraUpdateFactory.LatLongBoundsCameraPositionUpdate latLongBoundsUpdate, final CameraAnimationOptions animationOptions) {
        final LatLngBounds latLngBounds = latLongBoundsUpdate.getLatLngBounds();
        nativeAnimateCameraLatLongBoundsCameraPositionUpdate(m_jniEegeoMapApiPtr, latLngBounds, animationOptions);
    }


    @WorkerThread
    private static native void nativeMoveCameraIdentityCameraPositionUpdate(long jniEegeoMapApiPtr, CameraPosition cameraPostion);

    @WorkerThread
    private static native void nativeMoveCameraLatLongBoundsCameraPositionUpdate(long jniEegeoMapApiPtr, LatLngBounds latLngBounds);

    @WorkerThread
    private static native void nativeAnimateCameraIdentityCameraPositionUpdate(long jniEegeoMapApiPtr, CameraPosition cameraPostion, CameraAnimationOptions animationOptions);

    @WorkerThread
    private static native void nativeAnimateCameraLatLongBoundsCameraPositionUpdate(long jniEegeoMapApiPtr, LatLngBounds latLngBounds, CameraAnimationOptions animationOptions);

    @WorkerThread
    private static native void nativeCancelAnimation(long jniEegeoMapApiPtr);

    @WorkerThread
    private static native void nativeSetIndoorCameraRestriction(long jniEegeoMapApiPtr, boolean indoorCameraRestriction);

}
