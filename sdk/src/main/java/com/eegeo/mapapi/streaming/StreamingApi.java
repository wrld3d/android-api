package com.eegeo.mapapi.streaming;

import android.graphics.Point;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @eegeo.internal
 */
public class StreamingApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;

    private List<OnStreamingCompleteListener> m_onStreamingCompleteListeners = new ArrayList<>();

    public StreamingApi(INativeMessageRunner nativeRunner,
                        IUiMessageRunner uiRunner,
                        long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    INativeMessageRunner getNativeRunner() {
        return m_nativeRunner;
    }

    @UiThread
    IUiMessageRunner getUiRunner() {
        return m_uiRunner;
    }

    /**
     * Registers a listener of a streaming complete event
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void addStreamingCompleteListener(@NonNull OnStreamingCompleteListener listener) {
        m_onStreamingCompleteListeners.add(listener);
    }

    /**
     * Unregisters a listener of a streaming complete event
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeStreamingCompleteListener(@NonNull OnStreamingCompleteListener listener) {
        m_onStreamingCompleteListeners.remove(listener);
    }

    @UiThread
    public void notifyStreamingCompleteReceived() {
        for (OnStreamingCompleteListener listener: m_onStreamingCompleteListeners){
            listener.onStreamingComplete();
        }
    }
}


