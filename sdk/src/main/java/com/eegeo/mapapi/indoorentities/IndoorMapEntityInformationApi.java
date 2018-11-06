package com.eegeo.mapapi.indoorentities;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;

import java.security.InvalidParameterException;

/**
 * @eegeo.internal
 */
public class IndoorMapEntityInformationApi {
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<IndoorMapEntityInformation> m_nativeHandleToIndoorMapEntityInformation = new SparseArray<>();

    public IndoorMapEntityInformationApi(INativeMessageRunner nativeRunner,
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

    @WorkerThread
    public void register(IndoorMapEntityInformation indoorMapEntityInformation, IndoorMapEntityInformation.AllowHandleAccess allowHandleAccess) {
        int nativeHandle = indoorMapEntityInformation.getNativeHandle(allowHandleAccess);
        m_nativeHandleToIndoorMapEntityInformation.put(nativeHandle, indoorMapEntityInformation);
    }

    @WorkerThread
    public int create(String indoorMapId, IndoorMapEntityInformation.AllowHandleAccess allowHandleAccess) throws InvalidParameterException {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for internal use by IndoorMapEntityInformation");

        return nativeCreateIndoorMapEntityInformation(
                m_jniEegeoMapApiPtr,
                indoorMapId);
    }


    @WorkerThread
    public void destroy(IndoorMapEntityInformation indoorMapEntityInformation, IndoorMapEntityInformation.AllowHandleAccess allowHandleAccess) {
//        if (allowHandleAccess == null)
//            throw new NullPointerException("Null access token. Method is intended for internal use by IndoorMapEntityInformation");
//
//        final int nativeHandle = indoorMapEntityInformation.getNativeHandle(allowHandleAccess);
//
//        if (m_nativeHandleToIndoorMapEntityInformation.get(nativeHandle) != null) {
//            nativeDestroyIndoorEntityInformation(m_jniEegeoMapApiPtr, nativeHandle);
//            m_nativeHandleToIndoorMapEntityInformation.remove(nativeHandle);
//        }
    }

    @WorkerThread
    public void notifyIndoorEntityInformationReceived(final int nativeHandle) {
//        if (m_nativeHandleToIndoorMapEntityInformation.get(nativeHandle) != null) {
//            fetchIndoorEntityInformation(nativeHandle);
//        }
    }

    @WorkerThread
    private void fetchIndoorEntityInformation(int nativeHandle)
    {
//        final IndoorMapEntityInformation indoorEntityInformation = m_nativeHandleToIndoorMapEntityInformation.get(nativeHandle);
//
//        if (indoorEntityInformation == null)
//            throw new NullPointerException("IndoorMapEntityInformation object not found for nativeHandle");
//
//        final IndoorMapEntityInformation newIndoorEntityInformation = nativeGetIndoorEntityInformation(m_jniEegeoMapApiPtr, nativeHandle);
//        if (newIndoorEntityInformation != null) {
//            m_uiRunner.runOnUiThread(new Runnable() {
//                @UiThread
//                @Override
//                public void run() {
//                    indoorEntityInformation.setIndoorEntityInformation(newIndoorEntityInformation);
//                }
//            });
//        }
    }

    @WorkerThread
    private native int nativeCreateIndoorMapEntityInformation(
            long jniEegeoMapApiPtr,
            String indoorMapId
    );

//    @WorkerThread
//    private native void nativeDestroyIndoorEntityInformation(
//            long jniEegeoMapApiPtr,
//            int nativeHandle
//    );
//
//    @WorkerThread
//    private native IndoorMapEntityInformation nativeGetIndoorEntityInformation(
//            long jniEegeoMapApiPtr,
//            int nativeHandle
//    );

}


