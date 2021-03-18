package com.eegeo.mapapi.polygons;


import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.List;
import java.util.concurrent.Callable;

public class Polygon extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final PolygonApi m_polygonApi;
    private String m_indoorMapId;
    private int m_indoorFloorId;
    private double m_elevation;
    private ElevationMode m_elevationMode;

    private List<LatLng> m_points;
    private List<List<LatLng>> m_holes;
    private int m_fillColorARGB;


    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addPolygon to create a polygon
     *
     * @eegeo.internal
     */
    @UiThread
    public Polygon(@NonNull final PolygonApi polygonApi,
                  @NonNull final PolygonOptions polygonOptions) {
        super(polygonApi.getNativeRunner(), polygonApi.getUiRunner(),
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return polygonApi.create(polygonOptions, m_allowHandleAccess);
                    }
                });

        m_polygonApi = polygonApi;
        m_indoorMapId = polygonOptions.getIndoorMapId();
        m_indoorFloorId = polygonOptions.getIndoorFloorId();
        m_elevation = polygonOptions.getElevation();
        m_elevationMode = polygonOptions.getElevationMode();
        m_points = polygonOptions.getPoints();
        m_holes = polygonOptions.getHoles();
        m_fillColorARGB = polygonOptions.getFillColor();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                polygonApi.register(Polygon.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the current elevation of the polygon. The property is interpreted differently,
     * depending on the ElevationMode property.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this polygon.
     *
     * @param elevation A height in meters. Interpretation depends on the current
     *                  PolygonOptions.MarkerElevationMode.
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
     * Sets the elevation mode for this polygon.
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property.
     */
    @UiThread
    public void setElevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateNativeElevation();
    }

    /**
     * Gets the identifier of an indoor map on which this polygon should be displayed, if any.
     *
     * @return For a polygon on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Sets the identifier of an indoor map on which this polygon should be displayed, if any.
     *
     */
    @UiThread
    public void setIndoorMapId(String indoorMapId) {
        m_indoorMapId = indoorMapId;
        updateNativeIndoorMap();
    }

    /**
     * Gets the identifier of an indoor map floor on which this polygon should be displayed, if any.
     *
     * @return The indoor map floor id.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Sets the identifier of an indoor map floor on which this polygon should be displayed, if any.
     *
     * @param indoorFloorId The indoor map floor id.
     */
    @UiThread
    public void setIndoorFloorId(int indoorFloorId) {
        m_indoorFloorId = indoorFloorId;
        updateNativeIndoorMap();
    }

    /**
     * Gets the fill color of the polygon.
     *
     * @return The fill color of the polygon as a 32-bit ARGB color.
     */
    @UiThread
    public int getFillColor() {
        return m_fillColorARGB;
    }

    /**
     * Sets the fill color for this polygon.
     *
     * @param fillColor The fill color of the polygon as a 32-bit ARGB color.
     */
    @UiThread
    public void setFillColor(int fillColor) {
        m_fillColorARGB = fillColor;
        updateNativeStyleAttributes();
    }

    /**
     * Gets the outline points of the polygon.
     *
     * @return The vertices of the exterior ring (outline) of this polygon.
     */
    @UiThread
    public List<LatLng> getPoints() {
        return m_points;
    }

    /**
     * Gets the points that define holes for this polygon.
     *
     * @return A list of lists - each inner list contains the vertices of an interior ring (hole) of this polygon.
     */
    @UiThread
    public List<List<LatLng>> getHoles() {
        return m_holes;
    }

    /**
     * Removes this polygon from the map and destroys the polygon. Use EegeoMap.removePolygon
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_polygonApi.destroy(Polygon.this, Polygon.m_allowHandleAccess);
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
                m_polygonApi.setIndoorMap(
                        getNativeHandle(),
                        Polygon.m_allowHandleAccess,
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
                m_polygonApi.setElevation(
                        getNativeHandle(),
                        Polygon.m_allowHandleAccess,
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
                m_polygonApi.setStyleAttributes(
                        getNativeHandle(),
                        Polygon.m_allowHandleAccess,
                        fillColorARGB);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by PolygonApi");

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
