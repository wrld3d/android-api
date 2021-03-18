package com.eegeo.mapapi.props;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

public class Prop extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final PropsApi m_propsApi;
    private String m_indoorMapId;
    private int m_indoorFloorId;
    private double m_elevation;
    private ElevationMode m_elevationMode;
    private LatLng m_position;
    private double m_headingDegrees;
    private String m_geometryId;


    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addProp to create a prop
     *
     * @eegeo.internal
     */
    @UiThread
    public Prop(@NonNull final PropsApi propsApi,
                @NonNull final PropOptions propOptions) {
        super(propsApi.getNativeRunner(), propsApi.getUiRunner(),
                new Callable<Integer>() {
                    @Override
                    public Integer call() {
                        return propsApi.create(propOptions, m_allowHandleAccess);
                    }
                });

        m_propsApi = propsApi;
        m_indoorMapId = propOptions.getIndoorMapId();
        m_indoorFloorId = propOptions.getIndoorFloorId();
        m_elevation = propOptions.getElevation();
        m_elevationMode = propOptions.getElevationMode();
        m_position = propOptions.getPosition();
        m_headingDegrees = propOptions.getHeadingDegrees();
        m_geometryId = propOptions.getGeometryId();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                propsApi.register(Prop.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the current elevation of the prop. The property is interpreted differently,
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
     *                  value of the ElevationMode property.
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
     * Sets the elevation mode for this prop.
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property.
     */
    @UiThread
    public void setElevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateNativeElevationMode();
    }

    /**
     * Gets the identifier of an indoor map on which this prop should be displayed, if any.
     *
     * @return For a prop on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Gets the identifier of an indoor map floor on which this prop should be displayed, if any.
     *
     * @return The indoor map floor id.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Gets the heading of the prop, in degrees, clockwise from North (0 degrees).
     *
     * @return The heading of this prop in degrees.
     */
    @UiThread
    public double getHeadingDegrees() {
        return m_headingDegrees;
    }

    /**
     * Sets the heading of the prop, in degrees, clockwise from North (0 degrees).
     *
     * @param headingDegrees The heading of this prop in degrees.
     */
    @UiThread
    public void setHeadingDegrees(double headingDegrees) {
        m_headingDegrees = headingDegrees;
        updateNativeHeadingDegrees();
    }

    /**
     * Gets the position at which the prop will be rendered.
     *
     * @return The position of the prop as a LatLng.
     */
    @UiThread
    public LatLng getPosition() {
        return m_position;
    }

    /**
     * Sets the position at which the prop will be rendered.
     *
     * @param position The position of the prop as a LatLng.
     */
    @UiThread
    public void setPosition(LatLng position) {
        m_position = position;
        updateNativePosition();
    }

    /**
     * Gets the id of the geometry to be rendered in the location specified by the prop.
     * @return The id of the geometry to be rendered.  Available geometry is currently
     *                   curated by WRLD, please get in touch via support@wrld3d.com to discuss
     *                   additions.
     */
    @UiThread
    public String getGeometryId() {
        return m_geometryId;
    }

    /**
     * Set the id of the geometry to be rendered in the location specified by the prop.
     *
     * @param geometryId The id of the geometry to be rendered.  Available geometry is currently
     *                   curated by WRLD, please get in touch via support@wrld3d.com to discuss
     *                   additions.
     */
    @UiThread
    public void setGeometryId(String geometryId) {
        m_geometryId = geometryId;
        updateNativeGeometryId();
    }

    /**
     * Removes this prop from the map and destroys the prop. Use EegeoMap.removeProp
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_propsApi.destroy(Prop.this, Prop.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    private void updateNativePosition() {
        final LatLng position = m_position;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_propsApi.setPosition(
                        getNativeHandle(),
                        Prop.m_allowHandleAccess,
                        position);
            }
        });
    }

    @UiThread
    private void updateNativeGeometryId() {
        final String geometryId = m_geometryId;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_propsApi.setGeometryId(
                        getNativeHandle(),
                        Prop.m_allowHandleAccess,
                        geometryId);
            }
        });
    }

    @UiThread
    private void updateNativeElevation() {
        final double elevation = m_elevation;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_propsApi.setElevation(
                        getNativeHandle(),
                        Prop.m_allowHandleAccess,
                        elevation);
            }
        });
    }

    @UiThread
    private void updateNativeElevationMode() {
        final ElevationMode elevationMode = m_elevationMode;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_propsApi.setElevationMode(
                        getNativeHandle(),
                        Prop.m_allowHandleAccess,
                        elevationMode);
            }
        });
    }

    @UiThread
    private void updateNativeHeadingDegrees() {
        final double headingDegrees = m_headingDegrees;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_propsApi.setHeadingDegrees(
                        getNativeHandle(),
                        Prop.m_allowHandleAccess,
                        headingDegrees);
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
