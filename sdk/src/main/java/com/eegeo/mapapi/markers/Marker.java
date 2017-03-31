package com.eegeo.mapapi.markers;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

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
public class Marker extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final MarkerApi m_markerApi;
    private final String m_styleName;
    private final String m_userData;
    private final String m_indoorMapId;
    private final int m_indoorFloorId;
    private LatLng m_position;
    private double m_elevation;
    private MarkerOptions.MarkerElevationMode m_elevationMode;
    private String m_title;
    private String m_iconKey;
    private int m_drawOrder;

    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addMarker to create a marker
     *
     * @eegeo.internal
     */
    @UiThread
    public Marker(@NonNull final MarkerApi markerApi,
                  @NonNull final MarkerOptions markerOptions) {
        super(markerApi.getNativeRunner(), markerApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return markerApi.createMarker(markerOptions, m_allowHandleAccess);
                    }
                });

        m_markerApi = markerApi;
        m_position = markerOptions.getPosition();
        m_elevation = markerOptions.getElevation();
        m_elevationMode = markerOptions.getElevationMode();
        m_title = markerOptions.getTitle();
        m_iconKey = markerOptions.getIconKey();
        m_drawOrder = markerOptions.getDrawOrder();
        m_styleName = markerOptions.getStyleName();
        m_indoorMapId = markerOptions.getIndoorMapId();
        m_indoorFloorId = markerOptions.getIndoorFloorId();
        m_userData = markerOptions.getUserData();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                markerApi.registerMarker(Marker.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the position of the marker.
     *
     * @return A LatLng object representing the location of the marker on the map's surface.
     */
    @UiThread
    public LatLng getPosition() {
        return m_position;
    }

    /**
     * Sets the location of this marker.
     *
     * @param position A LatLng coordinate.
     */
    @UiThread
    public void setPosition(@NonNull LatLng position) {
        m_position = position;
        updateLocation();
    }

    /**
     * Returns the current elevation of the marker. The property is interpreted differently,
     * depending on the ElevationMode property.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this marker
     *
     * @param elevation A height in meters. Interpretation depends on the current
     *                  MarkerOptions.MarkerElevationMode
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
    public MarkerOptions.MarkerElevationMode getElevationMode() {
        return m_elevationMode;
    }

    /**
     * Sets the elevation mode for this marker
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property
     */
    @UiThread
    public void setElevationMode(MarkerOptions.MarkerElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateLocation();
    }

    /**
     * Gets the title of the marker
     *
     * @return The marker's title text.
     */
    @UiThread
    public String getTitle() {
        return m_title;
    }

    /**
     * Sets the title text to be displayed by this Marker
     *
     * @param title The text to display. May be empty
     */
    @UiThread
    public void setTitle(@NonNull String title) {
        m_title = title;
        updateLabel();
    }

    /**
     * Gets the key of the icon to display on the marker.
     *
     * @return A string key for the associated icon.
     */
    @UiThread
    public String getIconKey() {
        return m_iconKey;
    }

    /**
     * Sets the string key associated with the icon to be displayed by this Marker
     *
     * @param iconKey
     */
    @UiThread
    public void setIconKey(@NonNull String iconKey) {
        m_iconKey = iconKey;
        updateLabel();
    }

    /**
     * Gets the draw order of the marker.
     *
     * @return The marker's draw order.
     */
    @UiThread
    public int getDrawOrder() {
        return m_drawOrder;
    }

    /**
     * Sets the draw order for this marker. Draw order is used to determine which marker to display
     * when markers would otherwise overlap on the screen. The marker with the lowest drawOrder
     * value takes precedence.
     *
     * @param drawOrder Markers have a default drawOrder of 0.
     */
    @UiThread
    public void setDrawOrder(int drawOrder) {
        m_drawOrder = drawOrder;
        updateLabel();
    }

    /**
     * Gets the identifier of an indoor map on which this marker should be displayed, if any.
     *
     * @return For a marker on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Gets the identifier of an indoor map floor on which this marker should be displayed, if any.
     *
     * @return For a marker on an indoor map, the identifier of the floor; otherwise 0.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }

    /**
     * Gets the user data associated with this marker.
     *
     * @return The user data string.
     */
    @UiThread
    public String getUserData() {
        return m_userData;
    }

    /**
     * Gets the style name used for this marker. Currently not
     *
     * @return The style name used internally when binding a view for this marker
     */
    @UiThread
    public String getStyleName() {
        return m_styleName;
    }

    /**
     * Removes this marker from the map and destroys the marker. Use EegeoMap.removeMarker
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_markerApi.destroy(Marker.this, Marker.m_allowHandleAccess);
                m_markerApi.unregisterMarker(Marker.this, Marker.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    private void updateLocation() {
        final LatLng position = m_position;
        final double elevation = m_elevation;
        final MarkerOptions.MarkerElevationMode elevationMode = m_elevationMode;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_markerApi.updateLocation(getNativeHandle(), Marker.m_allowHandleAccess, position, elevation, elevationMode);
            }
        });
    }

    @UiThread
    private void updateLabel() {
        final String title = m_title;
        final String iconKey = m_iconKey;
        final int drawOrder = m_drawOrder;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_markerApi.updateLabel(getNativeHandle(), Marker.m_allowHandleAccess, title, iconKey, drawOrder);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by MarkerApi");

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
