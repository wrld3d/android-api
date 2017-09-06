package com.eegeo.mapapi.bluesphere;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

/**
 * A BlueSphere is a model that can be used for GPS and indoor positioning systems to show the users positon and oritentation.
 * <br>
 * BlueSphere has the following properties:
 *
 * <br>
 * <br>
 * <b>Position</b><br>
 * The LatLng location of the blue sphere on the map. The position can be changed at any point if you
 * want to move the blue sphere.
 * <br>
 * <br>
 * <b>Bearing</b><br>
 * The bearing the blue sphere is facing in degrees from north.
 *
 * <br>
 * <br>
 * <b>Elevation</b><br>
 * For outdoor maps, the height above ground of the sphere model's center, in meters.
 * For indoor maps, the height above the floor of the base of the leader-line, in meters.
 * <br>
 * <br>
 * <b>IndoorMapId</b><br>
 * Blue spheres can be displayed on indoor maps. This property stores the string identifier of the
 * indoor map on which the blue sphere is to be displayed. For outdoor blue spheres, this property is empty.
 * When setting an IndoorMapId the IndoorFloorId must also be specified.
 *
 * <br>
 * <br>
 * <b>IndoorFloorId</b><br>
 * For a blue sphere displayed on an indoor map, the identifier of the floor on which the blue sphere is to be
 * displayed.
 *
 * <br>
 * <br>
 * <b>Enabled</b><br>
 * The blue sphere is disabled by default and can be set to enabled via this method.
 *
 * <br>
 * <br>
 * Public methods in this class must be called on the Android UI thread.
 */
public class BlueSphere extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final BlueSphereApi m_bluesphereApi;
    private String m_indoorMapId;
    private int m_indoorFloorId;
    private LatLng m_position;
    private double m_bearing;
    private double m_elevation;
    private boolean m_enabled;

    /**
     * This constructor is for internal SDK use only -- use EegeoMap.GetBlueSphere() to access the blue sphere
     *
     * @eegeo.internal
     */
    @UiThread
    public BlueSphere(@NonNull final BlueSphereApi bluesphereApi) {
        super(bluesphereApi.getNativeRunner(), bluesphereApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return 0;
                    }
                });

        m_bluesphereApi = bluesphereApi;
        m_position = new LatLng(0.0, 0.0);
        m_elevation = 0.0;
        m_indoorMapId = null;
        m_indoorFloorId = 0;
        m_enabled = false;
    }

    /**
     * Returns the position of the blue sphere.
     *
     * @return A LatLng object representing the location of the blue sphere on the map's surface.
     */
    @UiThread
    public LatLng getPosition() {
        return m_position;
    }
    /**
     * Sets the location of this blue sphere.
     *
     * @param position A LatLng coordinate.
     */
    @UiThread
    public void setPosition(@NonNull LatLng position) {
        m_position = position;
        updateCoordinate();
    }

    /**
     * Returns the bearing of the blue sphere.
     *
     * @return A double representing the bearing of the blue sphere.
     */
    @UiThread
    public double getBearing() {
        return m_bearing;
    }

    /**
     * Sets the bearing of this blue sphere.
     *
     * @param bearing A double bearing in degrees.
     */
    @UiThread
    public void setBearing(@NonNull double bearing) {
        m_bearing = bearing;
        updateBearing();
    }

    /**
     * For outdoor maps, the height above ground of the sphere model's center, in meters.
     * For indoor maps, the height above the floor of the base of the leader-line, in meters.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this blue sphere.
     *
     * @param elevation For outdoor maps, the height above ground of the sphere model's center, in meters.
     *                  For indoor maps, the height above the floor of the base of the leader-line, in meters.
     */
    @UiThread
    public void setElevation(double elevation) {
        m_elevation = elevation;
        updateElevation();
    }

    /**
     * Gets the identifier of an indoor map on which this blue sphere should be displayed, if any.
     *
     * @return For a blue sphere on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Sets the indoor map and floor id of this blue sphere.
     *
     * @param indoorMapId For a blue sphere on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     * @param floorId For a blue sphere on an indoor map, the identifier of the floor; otherwise 0.
     */
    @UiThread
    public void setIndoorMap(String indoorMapId, int floorId) {
        m_indoorMapId = indoorMapId;
        m_indoorFloorId = floorId;
        updateIndoors();
    }

    /**
     * Gets the identifier of an indoor map floor on which this blue sphere should be displayed, if any.
     *
     * @return For a blue sphere on an indoor map, the identifier of the floor; otherwise 0.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Sets the identifier of an indoor map floor on which this blue sphere should be displayed, if any.
     *
     * @param floorId For a blue sphere on an indoor map, the identifier of the floor; otherwise 0.
     */
    @UiThread
    public void setIndoorFloorId(int floorId) {
        m_indoorFloorId = floorId;
    }

    /**
     * Gets the boolean which indicates if the blue sphere is enabled.
     *
     * @return A boolean that enables the blue sphere.
     */
    @UiThread
    public boolean getEnabled() {
        return m_enabled;
    }

    /**
     * Sets the boolean which enables the blue sphere to display.
     *
     * @param enabled A boolean that enables the blue sphere.
     */
    @UiThread
    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
        updateEnabled();
    }

    @UiThread
    private void updateEnabled() {
        final boolean enabled = m_enabled;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_bluesphereApi.setEnabled(BlueSphere.m_allowHandleAccess, enabled);
            }
        });
    }

    @UiThread
    private void updateBearing() {
        final double bearing = m_bearing;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_bluesphereApi.setBearing(BlueSphere.m_allowHandleAccess, bearing);
            }
        });
    }

    @UiThread
    private void updateCoordinate() {
        final LatLng position = m_position;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_bluesphereApi.setCoordinate(BlueSphere.m_allowHandleAccess, position);
            }
        });
    }
    @UiThread
    private void updateElevation() {
        final double elevation = m_elevation;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_bluesphereApi.setElevation(BlueSphere.m_allowHandleAccess, elevation);
            }
        });
    }

    @UiThread
    private void updateIndoors() {
        final String indoorMap = m_indoorMapId;
        final int floorId = m_indoorFloorId;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_bluesphereApi.setIndoorMap(BlueSphere.m_allowHandleAccess, indoorMap, floorId);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by BlueSphereApi");

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
