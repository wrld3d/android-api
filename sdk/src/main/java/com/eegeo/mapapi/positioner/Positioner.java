package com.eegeo.mapapi.positioner;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.util.NativeApiObject;
import com.eegeo.mapapi.util.Promise;

import java.util.concurrent.Callable;

public class Positioner extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final PositionerApi m_positionerApi;
    private final String m_indoorMapId;
    private final int m_indoorFloorId;
    private LatLng m_position;
    private double m_elevation;
    private ElevationMode m_elevationMode;
    private Point m_screenPoint = new Point();

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
     private Point m_screenPoint = new Point();
     * Returns the mode specifying how the Elevation property is interpreted.
     *
     * @return An enumerated value indicating whether Elevation is specified as a height above
     * terrain, or an absolute altitude above sea level.
     */
    @UiThread
    public Point getScreenPoint() {
        return m_screenPoint;
    }

    /**
     * Sets the elevation mode for this positioner
     *
     * @param screenPoint The mode specifying how to interpret the Elevation property
     */
    @UiThread
    public void setScreenPoint(Point screenPoint) {
        m_screenPoint = screenPoint;
    }

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

//    @UiThread
//    public Promise<Point> getScreenPoint() {
//        final Promise<Point> p = new Promise<>();
//        m_nativeRunner.runOnNativeThread(new Runnable() {
//            @Override
//            public void run() {
//                final Point screenPoint = m_positionerApi.getScreenPoint(getNativeHandle(), Positioner.m_allowHandleAccess);
//                m_uiRunner.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        p.ready(screenPoint);
//                    }
//                });
//            }
//        });
//        return p;
//    }

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

    static final class AllowHandleAccess {
        @WorkerThread
        private AllowHandleAccess() {
        }
    }
}
