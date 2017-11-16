package com.eegeo.mapapi.services.mapscene;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.services.poi.MapsceneRequestResponse;

import java.util.concurrent.Callable;


public class MapsceneApi {

    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<MapsceneRequest> m_nativeHandleToMapsceneRequest = new SparseArray<>();
    private MapsceneApplier m_mapsceneApplier;

    public MapsceneApi(INativeMessageRunner nativeRunner, IUiMessageRunner uiRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
        this.m_mapsceneApplier = null;
    }

    @UiThread
    public MapsceneRequest requestMapscene(final MapsceneRequestOptions options) {
        MapsceneRequest request = new MapsceneRequest(
                this,
                options.getApplyOnLoad(),
                options.getOnMapsceneRequestCompletedListener(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return beginMapsceneRequest(options);
                    }
                });
        return request;
    }

    @WorkerThread
    void register(MapsceneRequest mapsceneRequest, int nativeHandle) {
        m_nativeHandleToMapsceneRequest.put(nativeHandle, mapsceneRequest);
    }

    @WorkerThread
    void unregister(int nativeHandle) {
        m_nativeHandleToMapsceneRequest.remove(nativeHandle);
    }


    @WorkerThread
    public void notifyRequestComplete(final int nativeHandle, final boolean succeeded, final Mapscene mapscene) {
        MapsceneRequestResponse result = new MapsceneRequestResponse(succeeded, mapscene);
        if (m_nativeHandleToMapsceneRequest.get(nativeHandle) != null) {
            returnMapsceneResponse(nativeHandle, result);
        }
    }

    @WorkerThread
    void returnMapsceneResponse(final int nativeHandle, final MapsceneRequestResponse response) {
        final MapsceneRequest mapsceneRequest = m_nativeHandleToMapsceneRequest.get(nativeHandle);

        if (mapsceneRequest == null)
            throw new NullPointerException("MapsceneRequest object not found for nativeHandle");

        m_uiRunner.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(response.succeeded() &&
                   mapsceneRequest.shouldApplyOnLoad() &&
                   m_mapsceneApplier != null)
                {
                    m_mapsceneApplier.ApplyMapscene(response.getMapscene());
                }

                mapsceneRequest.returnResponse(response);
            }
        });

        m_nativeHandleToMapsceneRequest.remove(nativeHandle);
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
    int beginMapsceneRequest(final MapsceneRequestOptions options) {
        return nativeBeginMapsceneRequest(
                m_jniEegeoMapApiPtr,
                options.getUrlOrShortlink(),
                options.getApplyOnLoad());
    }


    @WorkerThread
    void cancelRequest(final int searchNativeHandle) {
        nativeCancelRequest(m_jniEegeoMapApiPtr, searchNativeHandle);
        m_nativeHandleToMapsceneRequest.remove(searchNativeHandle);
    }

    private native int nativeBeginMapsceneRequest(
            long jniEegeoMapApiPtr,
            String urlOrShortlink,
            boolean applyOnLoad
            );

    private native void nativeCancelRequest(long jniEegeoMapApiPtr, int requestNativeHandle);

    public void setMapsceneApplier(MapsceneApplier mapsceneApplier)
    {
        m_mapsceneApplier = mapsceneApplier;
    }
}
