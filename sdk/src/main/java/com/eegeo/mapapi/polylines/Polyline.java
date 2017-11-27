package com.eegeo.mapapi.polylines;


import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.List;
import java.util.concurrent.Callable;

public class Polyline extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final PolylineApi m_polylineApi;
    private String m_indoorMapId;
    private int m_indoorFloorId;
    private double m_elevation;
    private ElevationMode m_elevationMode;

    private List<LatLng> m_points;
    private float m_width;
    private int m_colorARGB;
    private float m_miterLimit;


    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addPolyline to create a polyline
     *
     * @eegeo.internal
     */
    @UiThread
    public Polyline(@NonNull final PolylineApi polylineApi,
                    @NonNull final PolylineOptions polylineOptions) {
        super(polylineApi.getNativeRunner(), polylineApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return polylineApi.create(polylineOptions, m_allowHandleAccess);
                    }
                });

        m_polylineApi = polylineApi;
        m_indoorMapId = polylineOptions.getIndoorMapId();
        m_indoorFloorId = polylineOptions.getIndoorFloorId();
        m_elevation = polylineOptions.getElevation();
        m_elevationMode = polylineOptions.getElevationMode();
        m_points = polylineOptions.getPoints();
        m_width = polylineOptions.getWidth();
        m_colorARGB = polylineOptions.getColor();
        m_miterLimit = polylineOptions.getMiterLimit();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                polylineApi.register(Polyline.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the current elevation of the polyline. The property is interpreted differently,
     * depending on the ElevationMode property.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this polyline.
     *
     * @param elevation A height in meters. Interpretation depends on the current
     *                  PolylineOptions.MarkerElevationMode.
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
     * Sets the elevation mode for this polyline.
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property.
     */
    @UiThread
    public void setElevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateNativeElevation();
    }

    /**
     * Gets the identifier of an indoor map on which this polyline should be displayed, if any.
     *
     * @return For a polyline on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Sets the identifier of an indoor map on which this polyline should be displayed, if any.
     *
     */
    @UiThread
    public void setIndoorMapId(String indoorMapId) {
        m_indoorMapId = indoorMapId;
        updateNativeIndoorMap();
    }

    /**
     * Gets the identifier of an indoor map floor on which this polyline should be displayed, if any.
     *
     * @return The indoor map floor id.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Sets the identifier of an indoor map floor on which this polyline should be displayed, if any.
     *
     * @param indoorFloorId The indoor map floor id.
     */
    @UiThread
    public void setIndoorFloorId(int indoorFloorId) {
        m_indoorFloorId = indoorFloorId;
        updateNativeIndoorMap();
    }

    /**
     * Gets the width of the polyline.
     *
     * @return The width of the polyline in screen pixels.
     */
    @UiThread
    public float getWidth() {
        return m_width;
    }

    /**
     * Sets the width of this polyline.
     *
     * @param width The width of the polyline in screen pixels.
     */
    @UiThread
    public void setWidth(float width) {
        m_width = width;
        updateNativeStyleAttributes();
    }

    /**
     * Gets the color of the polyline.
     *
     * @return The color of the polyline as a 32-bit ARGB color.
     */
    @UiThread
    public int getColor() {
        return m_colorARGB;
    }

    /**
     * Sets the color for this polyline.
     *
     * @param color The color of the polyline as a 32-bit ARGB color.
     */
    @UiThread
    public void setColor(int color) {
        m_colorARGB = color;
        updateNativeStyleAttributes();
    }

    /**
     * Gets the miter limit of the polyline.
     *
     * @return The miter limit, as a ratio between maximum allowed miter join diagonal length and
     * the line width.
     */
    @UiThread
    public float getMiterLimit() {
        return m_miterLimit;
    }

    /**
     * Sets the miter limit of this polyline.
     *
     * @param miterLimit the miter limit, as a ratio between maximum allowed miter join diagonal
     *                   length and the line width.
     */
    @UiThread
    public void setMiterLimit(float miterLimit) {
        m_miterLimit = miterLimit;
        updateNativeStyleAttributes();
    }

    /**
     * Gets the points of the polyline.
     *
     * @return The vertices of this polyline.
     */
    @UiThread
    public List<LatLng> getPoints() {
        return m_points;
    }

    /**
     * Removes this polyline from the map and destroys the polyline. Use EegeoMap.removePolyline
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_polylineApi.destroy(Polyline.this, Polyline.m_allowHandleAccess);
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
                m_polylineApi.setIndoorMap(
                        getNativeHandle(),
                        Polyline.m_allowHandleAccess,
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
                m_polylineApi.setElevation(
                        getNativeHandle(),
                        Polyline.m_allowHandleAccess,
                        elevation,
                        elevationMode);
            }
        });
    }

    @UiThread
    private void updateNativeStyleAttributes() {
        final float width = m_width;
        final int colorARGB = m_colorARGB;
        final float miterLimit = m_miterLimit;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_polylineApi.setStyleAttributes(
                        getNativeHandle(),
                        Polyline.m_allowHandleAccess,
                        width,
                        colorARGB,
                        miterLimit);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by PolylineApi");

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
