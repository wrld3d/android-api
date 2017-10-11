package com.eegeo.mapapi.positioning;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

//TDP???
/**
 * A Marker is an icon placed at a point on the map's surface. Markers may optionally have a text
 * title displayed alongside the icon. The Marker is displayed so that it is always oriented to
 * align with the device's screen, regardless of how the user moves or rotates the map.
 * <br>
 * Markers avoid being displayed such that one marker visually overlaps another. Instead, for any
 * set of markers, some may be temporarily hidden so that none are displayed intersecting another.
 * The decision about which markers to hide when markers overlap is based on the drawOrder property of
 * each marker.
 *
 * <br>
 * <br>
 * To create a Marker and add it to the map, use EegeoMap.addMarker()
 *
 * <br>
 * <br>
 * Marker has the following properties:
 * <br>
 * <br>
 * <b>Position</b><br>
 * The LatLng location of the marker on the map. The position can be changed at any point if you
 * want to move the marker.
 * <br>
 * <br>
 * <b>Elevation</b><br>
 * The height above the map at which the marker is located, in meters. Elevation can be specified as
 * either a height above terrain, or as an absolute altitude - see ElevationMode below.
 *
 * <br>
 * <br>
 * <b>ElevationMode</b><br>
 * Specifies how the Elevation property is interpreted, as either:
 * <br>
 * A height above the terrain (ground), in meters.
 * <br>
 * An absolute altitude (height above mean sea level), in meters.
 *
 *
 * <br>
 * <br>
 * <b>Title</b><br>
 * Text to display alongside the marker icon.
 *
 * <br>
 * <br>
 * <b>IconKey</b><br>
 * A string identifier specifying the name of an icon to display. An empty value will result in no
 * icon being displayed.
 *
 * <br>
 * <br>
 * <b>DrawOrder</b><br>
 * The draw order of the marker. Where markers would be drawn overlapping, marker with the lowest
 * draw order is displayed, and the other overlapping markers temporarily hidden. If overlapping
 * markers have equal draw order, then priority is based on screen position, with the marker that is
 * lowest on screen being drawn. The default value is 0.
 *
 * <br>
 * <br>
 * <b>UserData</b><br>
 * A String object associated with the marker. The API does not access this property - it is
 * provided for developer convenience, to avoid storing an independent association. You could use
 * this, for example, to store an ID from a data set.
 *
 * <br>
 * <br>
 * <b>IndoorMapId</b><br>
 * Markers can be displayed on indoor maps. This property stores the string identifier of the
 * indoor map on which the marker is to be displayed. For outdoor markers, this property is empty.
 * The property cannot be changed after construction - a Marker must be created as either an outdoor
 * marker (the default) or an indoor marker.
 *
 * <br>
 * <br>
 * <b>IndoorFloorId</b><br>
 * For a marker displayed on an indoor map, the identifier of the floor on which the marker is to be
 * displayed.
 *
 * <br>
 * <br>
 * Public methods in this class must be called on the Android UI thread.
 */
public class PointOnMap extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final PointOnMapApi m_pointOnMapApi;
    private final String m_userData;
    private final String m_indoorMapId;
    private final int m_indoorFloorId;
    private LatLng m_position;
    private double m_elevation;
    private ElevationMode m_elevationMode;

    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addPointOnMap to create a pointOnMap
     *
     * @eegeo.internal
     */
    @UiThread
    public PointOnMap(@NonNull final PointOnMapApi pointOnMapApi,
                  @NonNull final PointOnMapOptions pointOnMapOptions) {
        super(pointOnMapApi.getNativeRunner(), pointOnMapApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return pointOnMapApi.createPointOnMap(pointOnMapOptions, m_allowHandleAccess);
                    }
                });

        m_pointOnMapApi = pointOnMapApi;
        m_position = pointOnMapOptions.getPosition();
        m_elevation = pointOnMapOptions.getElevation();
        m_elevationMode = pointOnMapOptions.getElevationMode();
        m_indoorMapId = pointOnMapOptions.getIndoorMapId();
        m_indoorFloorId = pointOnMapOptions.getIndoorFloorId();
        m_userData = pointOnMapOptions.getUserData();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                pointOnMapApi.registerPointOnMap(PointOnMap.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the position of the point on map.
     *
     * @return A LatLng object representing the location of the point on map on the map's surface.
     */
    @UiThread
    public LatLng getPosition() {
        return m_position;
    }

    /**
     * Sets the location of this point on map.
     *
     * @param position A LatLng coordinate.
     */
    @UiThread
    public void setPosition(@NonNull LatLng position) {
        m_position = position;
        updateLocation();
    }

    /**
     * Returns the current elevation of the point on map. The property is interpreted differently,
     * depending on the ElevationMode property.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this point on map
     *
     * @param elevation A height in meters. Interpretation depends on the current
     *                  PointOnMapOptions.PointOnMapElevationMode
     */
    @UiThread
    public void setElevation(double elevation) {
        m_elevation = elevation;
        updateLocation();
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
     * Sets the elevation mode for this point on map
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property
     */
    @UiThread
    public void setElevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateLocation();
    }

    /**
     * Gets the identifier of an indoor map on which this point on map should be displayed, if any.
     *
     * @return For a point on map on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Gets the identifier of an indoor map floor on which this point on map should be displayed, if any.
     *
     * @return For a point on map on an indoor map, the identifier of the floor; otherwise 0.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Gets the user data associated with this point on map.
     *
     * @return The user data string.
     */
    @UiThread
    public String getUserData() {
        return m_userData;
    }

    /**
     * Removes this point on map from the map and destroys the point on map. Use EegeoMap.removePointOnMap
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_pointOnMapApi.destroy(PointOnMap.this, PointOnMap.m_allowHandleAccess);
                m_pointOnMapApi.unregisterPointOnMap(PointOnMap.this, PointOnMap.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    private void updateLocation() {
        final LatLng position = m_position;
        final double elevation = m_elevation;
        final ElevationMode elevationMode = m_elevationMode;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_pointOnMapApi.updateLocation(getNativeHandle(), PointOnMap.m_allowHandleAccess, position, elevation, elevationMode);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by PointOnMapApi");

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
