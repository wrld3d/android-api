package com.eegeo.mapapi.rendering;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;

import java.security.InvalidParameterException;

/**
 * @eegeo.internal
 */
public class RenderingApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private int m_nextNativeHandle = 1;

    /**
     * @eegeo.internal
     */
    public RenderingApi(INativeMessageRunner nativeRunner,
                        IUiMessageRunner uiRunner,
                        long jniEegeoMapApiPtr) {
        m_nativeRunner = nativeRunner;
        m_uiRunner = uiRunner;
        m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    public INativeMessageRunner getNativeRunner() { return m_nativeRunner; }

    @UiThread
    public IUiMessageRunner getUiRunner() {
        return m_uiRunner;
    }

    @WorkerThread
    public int createNativeHandle(EegeoMap.AllowApiAccess allowApiAccess) {
        if (allowApiAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by EegeoMap");

        if (m_nextNativeHandle != 1)
            throw new IllegalArgumentException("Native handle already created");

        return m_nextNativeHandle++;
    }

    @WorkerThread
    public void setMapCollapsed(EegeoMap.AllowApiAccess allowApiAccess, boolean isCollapsed) throws InvalidParameterException {
        if (allowApiAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by EegeoMap");

        nativeSetMapCollapsed(m_jniEegeoMapApiPtr, isCollapsed);
    }

    @WorkerThread
    private native void nativeSetMapCollapsed(long jniEegeoMapApiPtr, boolean isCollapsed);


}
