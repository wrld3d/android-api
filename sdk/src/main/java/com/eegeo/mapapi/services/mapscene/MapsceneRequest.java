package com.eegeo.mapapi.services.mapscene;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.services.poi.MapsceneRequestResponse;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

/**
 * A handle to an ongoing mapscene request.
 */
public class MapsceneRequest extends NativeApiObject {

    private MapsceneApi m_mapsceneApi;
    private final boolean m_applyOnLoad;
    private OnMapsceneRequestCompletedListener m_callback = null;

    @UiThread
    MapsceneRequest(final MapsceneApi mapsceneApi,
                    final boolean applyOnLoad,
                    OnMapsceneRequestCompletedListener callback,
                    Callable<Integer> beginRequestCallable) {
        super(mapsceneApi.getNativeRunner(), mapsceneApi.getUiRunner(), beginRequestCallable);

        m_mapsceneApi = mapsceneApi;
        m_applyOnLoad = applyOnLoad;
        m_callback = callback;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_mapsceneApi.register(MapsceneRequest.this, getNativeHandle());
            }
        });
    }

    public boolean shouldApplyOnLoad()
    {
        return m_applyOnLoad;
    }

    /**
     * Cancels the current mapscene request if it has not yet been completed.
     */
    @UiThread
    public void cancel() {
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_mapsceneApi.cancelRequest(getNativeHandle());
            }
        });
    }

    @UiThread
    void returnResponse(MapsceneRequestResponse mapsceneResponse) {
        if (m_callback != null) {
            m_callback.onMapsceneRequestCompleted(mapsceneResponse);
        }
    }
}
