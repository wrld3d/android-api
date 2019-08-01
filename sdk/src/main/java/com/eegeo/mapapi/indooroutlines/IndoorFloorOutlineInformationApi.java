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
public class IndoorFloorOutlineInformationApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<IndoorFloorOutlineInformation> m_nativeHandleToIndoorFloorOutlineInformation = new SparseArray<>();

    public IndoorFloorOutlineInformationApi(INativeMessageRunner nativeRunner,
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
    public void register(IndoorFloorOutlineInformation indoorFloorOutlineInformation, IndoorFloorOutlineInformation.AllowHandleAccess allowHandleAccess) {
        int nativeHandle = indoorFloorOutlineInformation.getNativeHandle(allowHandleAccess);
        m_nativeHandleToIndoorFloorOutlineInformation.put(nativeHandle, indoorFloorOutlineInformation);
        fetchIndoorFloorOutlineInformation(nativeHandle);
    }

    @WorkerThread
    public int create(String indoorMapId, int indoorMapFloorId, IndoorFloorOutlineInformation.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by IndoorMapEntityInformation");

        return nativeCreateIndoorFloorOutlineInformation(
                m_jniEegeoMapApiPtr,
                indoorMapId,
                indoorMapFloorId);
    }


    @WorkerThread
    public void destroy(IndoorFloorOutlineInformation indoorFloorOutlineInformation, IndoorFloorOutlineInformation.AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by IndoorMapEntityInformation");

        final int nativeHandle = indoorFloorOutlineInformation.getNativeHandle(allowHandleAccess);

        if (m_nativeHandleToIndoorFloorOutlineInformation.get(nativeHandle) != null) {
            nativeDestroyIndoorFloorOutlineInformation(m_jniEegeoMapApiPtr, nativeHandle);
            m_nativeHandleToIndoorFloorOutlineInformation.remove(nativeHandle);
        }
    }

    @WorkerThread
    public void notifyIndoorFloorOutlineInformationLoaded(final int nativeHandle) {
        if (m_nativeHandleToIndoorFloorOutlineInformation.get(nativeHandle) != null) {
            fetchIndoorFloorOutlineInformation(nativeHandle);
        }
    }

    @WorkerThread
    private void fetchIndoorFloorOutlineInformation(int nativeHandle)
    {
        final IndoorFloorOutlineInformation indoorFloorOutlineInformation = m_nativeHandleToIndoorFloorOutlineInformation.get(nativeHandle);

        if (indoorFloorOutlineInformation == null)
            throw new NullPointerException("IndoorFloorOutlineInformation object not found for nativeHandle");

        if (!nativeGetIndoorFloorOutlineInformationLoaded(m_jniEegeoMapApiPtr, nativeHandle)) {
            return;
        }

        final IndoorFloorOutlinePolygon indoorFloorOutlinePolygons[] = nativeGetIndoorFloorOutlinePolygon(m_jniEegeoMapApiPtr, nativeHandle);
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                indoorFloorOutlineInformation.updateFromNative(indoorFloorOutlinePolygons);
            }
        });
    }

    @WorkerThread
    private native int nativeCreateIndoorFloorOutlineInformation(
            long jniEegeoMapApiPtr,
            String indoorMapId,
            int indoorMapFloorId
    );

    @WorkerThread
    private native void nativeDestroyIndoorFloorOutlineInformation(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

    @WorkerThread
    private native boolean nativeGetIndoorFloorOutlineInformationLoaded(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );

    @WorkerThread
    private native IndoorFloorOutlinePolygon[] nativeGetIndoorFloorOutlinePolygon(
            long jniEegeoMapApiPtr,
            int nativeHandle
    );
}
