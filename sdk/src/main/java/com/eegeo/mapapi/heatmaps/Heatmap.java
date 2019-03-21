package com.eegeo.mapapi.heatmaps;


import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.List;
import java.util.concurrent.Callable;

public class Heatmap extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final HeatmapApi m_heatmapApi;
    private String m_indoorMapId;
    private int m_indoorFloorId;
    private double m_elevation;
    private ElevationMode m_elevationMode;

    private List<LatLng> m_points;
    private List<List<LatLng>> m_holes;
    private int m_fillColorARGB;


    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addHeatmap to create a heatmap
     *
     * @eegeo.internal
     */
    @UiThread
    public Heatmap(@NonNull final HeatmapApi heatmapApi,
                  @NonNull final HeatmapOptions heatmapOptions) {
        super(heatmapApi.getNativeRunner(), heatmapApi.getUiRunner(),
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return heatmapApi.create(heatmapOptions, m_allowHandleAccess);
                    }
                });

        m_heatmapApi = heatmapApi;
        m_indoorMapId = heatmapOptions.getIndoorMapId();
        m_indoorFloorId = heatmapOptions.getIndoorFloorId();
        m_elevation = heatmapOptions.getElevation();
        m_elevationMode = heatmapOptions.getElevationMode();
        m_points = heatmapOptions.getPoints();
        m_holes = heatmapOptions.getHoles();
        m_fillColorARGB = heatmapOptions.getFillColor();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                heatmapApi.register(Heatmap.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the current elevation of the heatmap. The property is interpreted differently,
     * depending on the ElevationMode property.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this heatmap.
     *
     * @param elevation A height in meters. Interpretation depends on the current
     *                  HeatmapOptions.MarkerElevationMode.
     */
    @UiThread
    public void setElevation(double elevation) {
        m_elevation = elevation;
        updateNativeElevation();
    }

    /**
     * Returns the mode specifying how the Elevation property is interpreted.
     *
     * @return An enumerated value indicating whether Elevation is specified as a height above
     * terrain, or an absolute altitude above sea level.
     */
    @UiThread
    public ElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Sets the elevation mode for this heatmap.
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property.
     */
    @UiThread
    public void setElevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateNativeElevation();
    }

    /**
     * Gets the identifier of an indoor map on which this heatmap should be displayed, if any.
     *
     * @return For a heatmap on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Sets the identifier of an indoor map on which this heatmap should be displayed, if any.
     *
     */
    @UiThread
    public void setIndoorMapId(String indoorMapId) {
        m_indoorMapId = indoorMapId;
        updateNativeIndoorMap();
    }

    /**
     * Gets the identifier of an indoor map floor on which this heatmap should be displayed, if any.
     *
     * @return The indoor map floor id.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Sets the identifier of an indoor map floor on which this heatmap should be displayed, if any.
     *
     * @param indoorFloorId The indoor map floor id.
     */
    @UiThread
    public void setIndoorFloorId(int indoorFloorId) {
        m_indoorFloorId = indoorFloorId;
        updateNativeIndoorMap();
    }

    /**
     * Gets the fill color of the heatmap.
     *
     * @return The fill color of the heatmap as a 32-bit ARGB color.
     */
    @UiThread
    public int getFillColor() {
        return m_fillColorARGB;
    }

    /**
     * Sets the fill color for this heatmap.
     *
     * @param fillColor The fill color of the heatmap as a 32-bit ARGB color.
     */
    @UiThread
    public void setFillColor(int fillColor) {
        m_fillColorARGB = fillColor;
        updateNativeStyleAttributes();
    }

    /**
     * Gets the outline points of the heatmap.
     *
     * @return The vertices of the exterior ring (outline) of this heatmap.
     */
    @UiThread
    public List<LatLng> getPoints() {
        return m_points;
    }

    /**
     * Gets the points that define holes for this heatmap.
     *
     * @return A list of lists - each inner list contains the vertices of an interior ring (hole) of this heatmap.
     */
    @UiThread
    public List<List<LatLng>> getHoles() {
        return m_holes;
    }

    /**
     * Removes this heatmap from the map and destroys the heatmap. Use EegeoMap.removeHeatmap
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_heatmapApi.destroy(Heatmap.this, Heatmap.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    private void updateNativeIndoorMap() {
        final String indoorMapId = m_indoorMapId;
        final int indoorFloorId = m_indoorFloorId;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setIndoorMap(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        indoorMapId,
                        indoorFloorId);
            }
        });
    }

    @UiThread
    private void updateNativeElevation() {
        final double elevation = m_elevation;
        final ElevationMode elevationMode = m_elevationMode;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setElevation(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        elevation,
                        elevationMode);
            }
        });
    }

    @UiThread
    private void updateNativeStyleAttributes() {
        final int fillColorARGB = m_fillColorARGB;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_heatmapApi.setStyleAttributes(
                        getNativeHandle(),
                        Heatmap.m_allowHandleAccess,
                        fillColorARGB);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by HeatmapApi");

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
