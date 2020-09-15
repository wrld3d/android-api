package com.eegeo.mapapi.indoorentities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;

import java.util.ArrayList;
import java.util.List;

public class IndoorEntityApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;

    private long m_jniEegeoMapApiPtr;
    private List<OnIndoorEntityPickedListener> m_onIndoorEntityPickedListeners = new ArrayList<OnIndoorEntityPickedListener>();

    public IndoorEntityApi(INativeMessageRunner nativeRunner,
                      IUiMessageRunner uiRunner,
                      long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    public void setIndoorEntityHighlights(@NonNull final String indoorMapId, @NonNull final List<String> indoorEntityIds, @NonNull final int highlightColor, @NonNull final float borderThickness){
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                nativeSetIndoorEntityHighlights(m_jniEegeoMapApiPtr, indoorMapId, indoorEntityIds, highlightColor, borderThickness);
            }
        });
    }

    @UiThread
    public void clearIndoorEntityHighlights(final String indoorMapId, final List<String> indoorEntityIds){
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                nativeClearIndoorEntityHighlights(m_jniEegeoMapApiPtr, indoorMapId, indoorEntityIds);
            }
        });
    }

    @UiThread
    public void clearAllIndoorEntityHighlights(){
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                nativeClearAllIndoorEntityHighlights(m_jniEegeoMapApiPtr);
            }
        });
    }

    @UiThread
    public void addOnIndoorEntityPickedListener(OnIndoorEntityPickedListener listener) {
        m_onIndoorEntityPickedListeners.add(listener);
    }

    @UiThread
    public void removeOnIndoorEntityPickedListener(OnIndoorEntityPickedListener listener) {
        m_onIndoorEntityPickedListeners.remove(listener);
    }

    @WorkerThread
    public void notifyIndoorEntityPicked(final IndoorEntityPickedMessage message) {
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                for (OnIndoorEntityPickedListener listener : m_onIndoorEntityPickedListeners) {
                    listener.onIndoorEntityPicked(message);
                }
            }
        });
    }


    @WorkerThread
    private native void nativeSetIndoorEntityHighlights(long jniEegeoMapApiPtr, String indoorMapId, List<String> indoorEntityIds, int highlightColor, float borderThickness);

    @WorkerThread
    private native void nativeClearIndoorEntityHighlights(long jniEegeoMapApiPtr, String indoorMapId, List<String> indoorEntityIds);

    @WorkerThread
    private native void nativeClearAllIndoorEntityHighlights(long jniEegeoMapApiPtr);

}
