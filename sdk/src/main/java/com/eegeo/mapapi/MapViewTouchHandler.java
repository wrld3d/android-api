package com.eegeo.mapapi;


import androidx.annotation.UiThread;
import android.view.MotionEvent;

final class MapViewTouchHandler {
    private EegeoNativeMapView m_eegeoNativeMapView;

    MapViewTouchHandler(EegeoNativeMapView eegeoNativeMapView) {
        this.m_eegeoNativeMapView = eegeoNativeMapView;
    }

    @UiThread
    public boolean onTouchEvent(MotionEvent e) {
        //we need to convert multi-touch event handling into struct of arrays for many pointers to send over JNI

        //C++ event representation is like;
        /*
			float x, y;
			int pointerIdentity;
			int pointerIndex;
		 */

        final int pointerCount = e.getPointerCount();
        final int primaryActionIndex = e.getActionIndex();
        final int primaryActionIdentifier = e.getPointerId(primaryActionIndex);

        final float[] xArray = new float[pointerCount];
        final float[] yArray = new float[pointerCount];
        final int[] pointerIdentityArray = new int[pointerCount];
        final int[] pointerIndexArray = new int[pointerCount];

        for (int pointerIndex = 0; pointerIndex < pointerCount; ++pointerIndex) {
            xArray[pointerIndex] = e.getX(pointerIndex);
            yArray[pointerIndex] = e.getY(pointerIndex);
            pointerIdentityArray[pointerIndex] = e.getPointerId(pointerIndex);
            pointerIndexArray[pointerIndex] = pointerIndex;
        }

        boolean handled = true;

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                m_eegeoNativeMapView.onPointerDown(primaryActionIndex, primaryActionIdentifier, pointerCount, xArray, yArray, pointerIdentityArray, pointerIndexArray);
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                m_eegeoNativeMapView.onPointerUp(primaryActionIndex, primaryActionIdentifier, pointerCount, xArray, yArray, pointerIdentityArray, pointerIndexArray);
                break;

            case MotionEvent.ACTION_MOVE:
                m_eegeoNativeMapView.onPointerMove(primaryActionIndex, primaryActionIdentifier, pointerCount, xArray, yArray, pointerIdentityArray, pointerIndexArray);
                break;
            default:
                handled = false;
        }

        return handled;
    }
}
