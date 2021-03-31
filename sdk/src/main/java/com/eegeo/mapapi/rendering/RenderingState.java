package com.eegeo.mapapi.rendering;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.bluesphere.BlueSphere;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

/**
 * @eegeo.internal
 */
public class RenderingState extends NativeApiObject {
    private final RenderingApi m_renderingApi;
    private final EegeoMap.AllowApiAccess m_allowApiAccess;

    private boolean m_isMapCollapsed;

    public RenderingState(
            final RenderingApi renderingApi,
            final EegeoMap.AllowApiAccess allowApiAccess,
            boolean isMapCollapsed
    ) {
        super(renderingApi.getNativeRunner(), renderingApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return renderingApi.createNativeHandle(allowApiAccess);
                    }
                });

        m_renderingApi = renderingApi;
        m_allowApiAccess = allowApiAccess;
        m_isMapCollapsed = isMapCollapsed;
        setMapCollapsed(isMapCollapsed);
    }

    @UiThread
    public boolean isMapCollapsed() {
        return m_isMapCollapsed;
    }

    @UiThread
    public void setMapCollapsed(final boolean isMapCollapsed) {
        m_isMapCollapsed = isMapCollapsed;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_renderingApi.setMapCollapsed(m_allowApiAccess, isMapCollapsed);
            }
        });
    }
}
