package com.eegeo.mapapi.labels;

import android.graphics.Point;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;

import java.security.InvalidParameterException;

/**
 * @eegeo.internal
 */
public class LabelApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;

    public LabelApi(INativeMessageRunner nativeRunner,
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

    public void setLabelsEnabled(final boolean enabled) {

        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            @WorkerThread
            public void run() {
                nativeSetLabelsEnabled(m_jniEegeoMapApiPtr, enabled);
            }
        });
    }

    @WorkerThread
    private native void nativeSetLabelsEnabled(
            long jniEegeoMapApiPtr,
            boolean enabled
    );
}
