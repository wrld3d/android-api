package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.util.NativeApiObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *  TODO: add summary
 */
public class IndoorFloorOutlineInformation extends NativeApiObject {
    private static final IndoorFloorOutlineInformation.AllowHandleAccess m_allowHandleAccess = new IndoorFloorOutlineInformation.AllowHandleAccess();
    private final IndoorFloorOutlineInformationApi m_indoorFloorOutlineInformationApi;
    private final String m_indoorMapId;
    private final int m_indoorMapFloorId;
    public List<IndoorFloorOutlinePolygon> m_outlinePolygons;
    private boolean m_isLoaded;
    private OnIndoorFloorOutlineInformationLoadedListener m_onIndoorFloorOutlineInformationLoadedListener;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorFloorOutlineInformation(
            @NonNull final IndoorFloorOutlineInformationApi indoorFloorOutlineInformationApi,
            @NonNull final String indoorMapId,
            @NonNull final int indoorMapFloorId,
            final OnIndoorFloorOutlineInformationLoadedListener indoorFloorOutlineInformationLoadedListener
    )
    {
        super(indoorFloorOutlineInformationApi.getNativeRunner(),
                indoorFloorOutlineInformationApi.getUiRunner(),
                new Callable<Integer>()
                {
                    @Override
                    public Integer call() throws Exception {
                        return indoorFloorOutlineInformationApi.create(indoorMapId, indoorMapFloorId, m_allowHandleAccess);
                    }
                });

        m_indoorFloorOutlineInformationApi = indoorFloorOutlineInformationApi;

        m_indoorMapId = indoorMapId;
        m_indoorMapFloorId = indoorMapFloorId;
        m_outlinePolygons = new ArrayList<>();
        m_isLoaded = false;
        m_onIndoorFloorOutlineInformationLoadedListener = indoorFloorOutlineInformationLoadedListener;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_indoorFloorOutlineInformationApi.register(IndoorFloorOutlineInformation.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Gets the string id of the indoor map associated with this IndoorFloorOutlineInformation object.
     * @return The indoor map id.
     */
    public String getIndoorMapId() { return m_indoorMapId; }

    /**
     * Gets the int id of the indoor map floor associated with this IndoorFloorOutlineInformation object.
     * @return The indoor map floor id.
     */
    public int getIndoorMapFloorId() { return m_indoorMapFloorId; }

    /**
     * Gets IndoorFloorOutlinePolygon objects if loaded.
     * @return The IndoorFloorOutlinePolygon objects for the indoor map floor.
     */
    public List<IndoorFloorOutlinePolygon> getIndoorMapFloorOutlinePolygons() { return m_outlinePolygons; }

    /**
     * Gets the current outline load status.
     * @return True if the outline has finished loading.
     */
    public boolean isLoaded() { return m_isLoaded; }


    /**
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_indoorFloorOutlineInformationApi.destroy(IndoorFloorOutlineInformation.this, IndoorFloorOutlineInformation.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    void updateFromNative(IndoorFloorOutlinePolygon indoorMapEntities[])
    {
        m_outlinePolygons = Arrays.asList(indoorMapEntities);
        m_isLoaded = true;
        if (m_onIndoorFloorOutlineInformationLoadedListener != null) {
            m_onIndoorFloorOutlineInformationLoadedListener.onIndoorMapEntityInformationLoaded(this);
        }
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by BuildingsApi");

        if (!hasNativeHandle())
            throw new IllegalStateException("Native handle not available");

        return getNativeHandle();
    }

    static final class AllowHandleAccess {
        @WorkerThread
        private AllowHandleAccess() {
        }
    }
}
