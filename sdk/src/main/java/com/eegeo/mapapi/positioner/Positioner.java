package com.eegeo.mapapi.positioner;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;
/**
 * A Positioner represents a single point on the map. The primary purpose of a positioner is to
 * expose a point on the map as a 2D coordinate in screen space. A positioner is not drawn however
 * it can be used to position a View.
 * <br>
 * <br>
 * To create a Positioner and add it to the map, use EegeoMap.addPositioner()
 * <br>
 * <br>
 * Positioner has the following properties:
 * <br>
 * <br>
 * <b>Position</b><br>
 * The LatLng location of the positioner on the map. The position can be changed at any point if you
 * want to move the positioner.
 * <br>
 * <br>
 * <b>Elevation</b><br>
 * The height above the map at which the positioner is located, in meters. Elevation can be
 * specified as either a height above terrain, or as an absolute altitude - see ElevationMode below.
 * <br>
 * <br>
 * <b>ElevationMode</b><br>
 * Specifies how the Elevation property is interpreted, as either:
 * <br>
 * A height above the terrain (ground), in meters.
 * <br>
 * An absolute altitude (height above mean sea level), in meters.
 * <br>
 * <br>
 * <b>IndoorMapId</b><br>
 * Positioners can be displayed on indoor maps. This property stores the string identifier of the
 * indoor map on which the positioner is to be located. For outdoor positioners, this property is
 * empty. The property cannot be changed after construction - a positioner must be created as either
 * an outdoor positioner (the default) or an indoor positioner.
 * <br>
 * <br>
 * <b>IndoorFloorId</b><br>
 * For a positioner displayed on an indoor map, the identifier of the floor on which the positioner
 * is to be displayed.
 * <br>
 * <br>
 * <b>PositionerChangedListener</b><br>
 * Each time the screen space coordinate of the positioner changes the PositionerChangedListener
 * will be called.
 * <br>
 * <br>
 * Public methods in this class must be called on the Android UI thread.
 */
public class Positioner extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final PositionerApi m_positionerApi;
    private final String m_indoorMapId;
    private final int m_indoorFloorId;
    private LatLng m_position;
    private double m_elevation;
    private ElevationMode m_elevationMode;
    private OnPositionerChangedListener m_positionerChangedListener;
    private Point m_screenPoint = new Point();
    private boolean m_isBehindGlobeHorizon = false;

    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addPositioner to create a positioner
     *
     * @eegeo.internal
     */
    @UiThread
    public Positioner(@NonNull final PositionerApi positionerApi,
                  @NonNull final PositionerOptions positionerOptions) {
        super(positionerApi.getNativeRunner(), positionerApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return positionerApi.createPositioner(positionerOptions, m_allowHandleAccess);
                    }
                });

        m_positionerApi = positionerApi;
        m_position = positionerOptions.getPosition();
        m_elevation = positionerOptions.getElevation();
        m_elevationMode = positionerOptions.getElevationMode();
        m_indoorMapId = positionerOptions.getIndoorMapId();
        m_indoorFloorId = positionerOptions.getIndoorFloorId();
        m_positionerChangedListener = positionerOptions.getPositionerChangedListener();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                positionerApi.registerPositioner(Positioner.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Returns the position of the positioner.
     *
     * @return A LatLng object representing the location of the positioner on the map's surface.
     */
    @UiThread
    public LatLng getPosition() {
        return m_position;
    }

    /**
     * Sets the location of this positioner.
     *
     * @param position A LatLng coordinate.
     */
    @UiThread
    public void setPosition(@NonNull LatLng position) {
        m_position = position;
        updateLocation();
    }

    /**
     * Returns the current elevation of the positioner. The property is interpreted differently,
     * depending on the ElevationMode property.
     *
     * @return A height, in meters.
     */
    @UiThread
    public double getElevation() {
        return m_elevation;
    }

    /**
     * Sets the elevation of this positioner
     *
     * @param elevation A height in meters. Interpretation depends on the current
     *                  PositionerOptions.PositionerElevationMode
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
     * Sets the elevation mode for this positioner
     *
     * @param elevationMode The mode specifying how to interpret the Elevation property
     */
    @UiThread
    public void setElevationMode(ElevationMode elevationMode) {
        m_elevationMode = elevationMode;
        updateLocation();
    }

    /**
     */
    @UiThread
    public Point getScreenPoint() {
        return m_screenPoint;
    }

    @UiThread
    public boolean isBehindGlobeHorizon() { return m_isBehindGlobeHorizon; }

    /**
     * Gets the identifier of an indoor map on which this positioner should be displayed, if any.
     *
     * @return For a positioner on an indoor map, the string identifier of the indoor map; otherwise an
     * empty string.
     */
    @UiThread
    public String getIndoorMapId() {
        return m_indoorMapId;
    }

    /**
     * Gets the identifier of an indoor map floor on which this positioner should be displayed, if any.
     *
     * @return For a positioner on an indoor map, the identifier of the floor; otherwise 0.
     */
    @UiThread
    public int getIndoorFloorId() {
        return m_indoorFloorId;
    }


    /**
     * Removes this positioner from the map and destroys the positioner. Use EegeoMap.removePositioner
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_positionerApi.destroy(Positioner.this, Positioner.m_allowHandleAccess);
                m_positionerApi.unregisterPositioner(Positioner.this, Positioner.m_allowHandleAccess);
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
                m_positionerApi.updateLocation(getNativeHandle(), Positioner.m_allowHandleAccess, position, elevation, elevationMode);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by PositionerApi");

        if (!hasNativeHandle())
            throw new IllegalStateException("Native handle not available");

        return getNativeHandle();
    }

    /**
     * @eegeo.internal
     */
    @UiThread
    void setProjectedState(
            Point screenPoint,
            boolean isBehindGlobeHorizon
    ) {
        if (!m_screenPoint.equals(screenPoint) ||
                m_isBehindGlobeHorizon != isBehindGlobeHorizon) {
            m_screenPoint = screenPoint;
            m_isBehindGlobeHorizon = isBehindGlobeHorizon;
            if (m_positionerChangedListener != null) {
                m_positionerChangedListener.onPositionerChanged(this);
            }
        }
    }

    static final class AllowHandleAccess {
        @WorkerThread
        private AllowHandleAccess() {
        }
    }
}
