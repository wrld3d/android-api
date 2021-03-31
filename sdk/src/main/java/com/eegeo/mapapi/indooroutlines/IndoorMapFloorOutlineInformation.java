package com.eegeo.mapapi.indooroutlines;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.util.NativeApiObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *  Maintains information about one or more polygons representing the outline of an indoor map floor with specified id.
 *  Outline information is updated as map tiles stream in. Change notification is available via
 *  the supplied OnIndoorMapFloorOutlineInformationLoadedListener.
 */
public class IndoorMapFloorOutlineInformation extends NativeApiObject {
    private static final IndoorMapFloorOutlineInformation.AllowHandleAccess m_allowHandleAccess = new IndoorMapFloorOutlineInformation.AllowHandleAccess();
    private final IndoorMapFloorOutlineInformationApi m_indoorMapFloorOutlineInformationApi;
    private final String m_indoorMapId;
    private final int m_indoorMapFloorId;
    private List<IndoorMapFloorOutlinePolygon> m_outlinePolygons;
    private boolean m_isLoaded;
    private OnIndoorMapFloorOutlineInformationLoadedListener m_onIndoorMapFloorOutlineInformationLoadedListener;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorMapFloorOutlineInformation(
            @NonNull final IndoorMapFloorOutlineInformationApi indoorMapFloorOutlineInformationApi,
            @NonNull final String indoorMapId,
            @NonNull final int indoorMapFloorId,
            final OnIndoorMapFloorOutlineInformationLoadedListener indoorMapFloorOutlineInformationLoadedListener
    )
    {
        super(indoorMapFloorOutlineInformationApi.getNativeRunner(),
                indoorMapFloorOutlineInformationApi.getUiRunner(),
                new Callable<Integer>()
                {
                    @Override
                    public Integer call() throws Exception {
                        return indoorMapFloorOutlineInformationApi.create(indoorMapId, indoorMapFloorId, m_allowHandleAccess);
                    }
                });

        m_indoorMapFloorOutlineInformationApi = indoorMapFloorOutlineInformationApi;

        m_indoorMapId = indoorMapId;
        m_indoorMapFloorId = indoorMapFloorId;
        m_outlinePolygons = new ArrayList<>();
        m_isLoaded = false;
        m_onIndoorMapFloorOutlineInformationLoadedListener = indoorMapFloorOutlineInformationLoadedListener;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_indoorMapFloorOutlineInformationApi.register(IndoorMapFloorOutlineInformation.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Gets the string id of the indoor map associated with this IndoorMapFloorOutlineInformation object.
     * @return The indoor map id.
     */
    public String getIndoorMapId() { return m_indoorMapId; }

    /**
     * Gets the int id of the indoor map floor associated with this IndoorMapFloorOutlineInformation object.
     * @return The indoor map floor id.
     */
    public int getIndoorMapFloorId() { return m_indoorMapFloorId; }

    /**
     * Gets IndoorMapFloorOutlinePolygon objects if loaded.
     * @return The IndoorMapFloorOutlinePolygon objects for the indoor map floor.
     */
    public List<IndoorMapFloorOutlinePolygon> getIndoorMapFloorOutlinePolygons() { return m_outlinePolygons; }

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
                m_indoorMapFloorOutlineInformationApi.destroy(IndoorMapFloorOutlineInformation.this, IndoorMapFloorOutlineInformation.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    void updateFromNative(IndoorMapFloorOutlinePolygon indoorMapFloorOutlinePolygons[])
    {
        m_outlinePolygons = Arrays.asList(indoorMapFloorOutlinePolygons);
        m_isLoaded = true;
        if (m_onIndoorMapFloorOutlineInformationLoadedListener != null) {
            m_onIndoorMapFloorOutlineInformationLoadedListener.onIndoorMapFloorOutlineInformationLoaded(this);
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
