package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;

import java.security.InvalidParameterException;

/**
 * @eegeo.internal
 */
public class IndoorMapFloorOutlineInformationApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<IndoorMapFloorOutlineInformation> m_nativeHandleToIndoorMapFloorOutlineInformation = new SparseArray<>();

    public IndoorMapFloorOutlineInformationApi(INativeMessageRunner nativeRunner,
                                               IUiMessageRunner uiRunner,
                                               long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    public INativeMessageRunner getNativeRunner() {
        return m_nativeRunner;
    }

    @UiThread
    public IUiMessageRunner getUiRunner() {
        return m_uiRunner;
    }

    @WorkerThread
    public void register(IndoorMapFloorOutlineInformation indoorMapFloorOutlineInformation, IndoorMapFloorOutlineInformation.AllowHandleAccess allowHandleAccess) {
        int nativeHandle = indoorMapFloorOutlineInformation.getNativeHandle(allowHandleAccess);
        m_nativeHandleToIndoorMapFloorOutlineInformation.put(nativeHandle, indoorMapFloorOutlineInformation);
        fetchIndoorMapFloorOutlineInformation(nativeHandle);
    }

    @WorkerThread
    public int create(String indoorMapId, int indoorMapFloorId, IndoorMapFloorOutlineInformation.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by IndoorMapEntityInformation");

        return nativeCreateIndoorMapFloorOutlineInformation(
                m_jniEegeoMapApiPtr,
                indoorMapId,
                indoorMapFloorId);
    }


    @WorkerThread
    public void destroy(IndoorMapFloorOutlineInformation indoorMapFloorOutlineInformation, IndoorMapFloorOutlineInformation.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by IndoorMapEntityInformation");

        final int nativeHandle = indoorMapFloorOutlineInformation.getNativeHandle(allowHandleAccess);

        if (m_nativeHandleToIndoorMapFloorOutlineInformation.get(nativeHandle) != null) {
            nativeDestroyIndoorMapFloorOutlineInformation(m_jniEegeoMapApiPtr, nativeHandle);
            m_nativeHandleToIndoorMapFloorOutlineInformation.remove(nativeHandle);
        }
    }

    @WorkerThread
    public void notifyIndoorMapFloorOutlineInformationLoaded(final int nativeHandle) {
        if (m_nativeHandleToIndoorMapFloorOutlineInformation.get(nativeHandle) != null) {
            fetchIndoorMapFloorOutlineInformation(nativeHandle);
        }
    }

    @WorkerThread
    private void fetchIndoorMapFloorOutlineInformation(int nativeHandle)
    {
        final IndoorMapFloorOutlineInformation indoorMapFloorOutlineInformation = m_nativeHandleToIndoorMapFloorOutlineInformation.get(nativeHandle);

        if (indoorMapFloorOutlineInformation == null)
            throw new NullPointerException("IndoorMapFloorOutlineInformation object not found for nativeHandle");

        if (!nativeGetIndoorMapFloorOutlineInformationLoaded(m_jniEegeoMapApiPtr, nativeHandle)) {
            return;
        }

        final IndoorMapFloorOutlinePolygon indoorMapFloorOutlinePolygons[] = nativeGetIndoorMapFloorOutlinePolygon(m_jniEegeoMapApiPtr, nativeHandle);
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                indoorMapFloorOutlineInformation.updateFromNative(indoorMapFloorOutlinePolygons);
            }
        });
    }

    @WorkerThread
    private native int nativeCreateIndoorMapFloorOutlineInformation(
            long jniEegeoMapApiPtr,
            String indoorMapId,
            int indoorMapFloorId
    );

    @WorkerThread
    private native void nativeDestroyIndoorMapFloorOutlineInformation(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

    @WorkerThread
    private native boolean nativeGetIndoorMapFloorOutlineInformationLoaded(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

    @WorkerThread
    private native IndoorMapFloorOutlinePolygon[] nativeGetIndoorMapFloorOutlinePolygon(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );
}
