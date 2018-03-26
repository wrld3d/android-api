package com.eegeo.mapapi.camera;

import android.graphics.Point;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLngAlt;
import com.eegeo.mapapi.util.Promise;

/**
 * Calculates the mapping between screen space points and world points.
 *
 * Calculation of this mapping requires an asynchronous operation, so results are encapsulated in
 * a Promise object.
 */
public class Projection {
    private final long m_eegeoMapApiPtr;
    private final INativeMessageRunner m_nativeRunner;
    private final IUiMessageRunner m_uiRunner;

    /**
     * This constructor is for internal SDK use only -- use EegeoMap.getProjection instead
     *
     * @eegeo.internal
     */
    public Projection(INativeMessageRunner nativeRunner,
                      IUiMessageRunner uiRunner,
                      long eegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_eegeoMapApiPtr = eegeoMapApiPtr;
    }

    /**
     * Maps a screen point to a point expressed as latitude, longitude, and altitude.
     *
     * @param point A screen space point, in units of pixels with the origin at the top left
     *              corner of the screen.
     * @return A promise to provide the real world location displayed on the screen at the point.
     * @eegeo.codeintro The value of the promise can be accessed through an object implementing the Ready interface, for example:
     * @eegeo.code <pre>
     * projection.fromScreenLocation(point)
     *           .then(new Ready&lt;LatLngAlt&gt;() {
     *               public void ready(LatLngAlt latLngAlt) {
     *                   // use value of latLngAlt here
     *               }
     *           }
     * );
     * </pre>
     */
    @UiThread
    public Promise<LatLngAlt> fromScreenLocation(final Point point) {
        final Promise<LatLngAlt> p = new Promise<LatLngAlt>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final double[] latLongAlt = nativeScreenToWorldPoint(m_eegeoMapApiPtr, point.x, point.y);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(new LatLngAlt(latLongAlt[0], latLongAlt[1], latLongAlt[2]));
                    }
                });
            }
        });
        return p;
    }

    /**
     * Maps a real world point to screen co-ordinates.
     *
     * @param location A real world point.
     * @return A promise to provide the screen space point corresponding to the real world location, in
     * units of pixels with the origin at the top left corner of the screen.
     * @eegeo.codeintro The value of the promise can be accessed through an object implementing the Ready interface, for example:
     * @eegeo.code <pre>
     * projection.toScreenLocation(latlngalt)
     *           .then(new Ready&lt;Point&gt;() {
     *               public void ready(Point pt) {
     *                   // use value of pt here
     *               }
     *           }
     * );
     * </pre>
     */
    @UiThread
    public Promise<Point> toScreenLocation(final LatLngAlt location) {
        final Promise<Point> p = new Promise<Point>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final double[] screen = nativeWorldToScreen(m_eegeoMapApiPtr, location.latitude, location.longitude, location.altitude);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(new Point((int) screen[0], (int) screen[1]));
                    }
                });
            }
        });
        return p;
    }

    private native double[] nativeWorldToScreen(long jniEegeoMapApiPtr, double lat, double lon, double alt);

    private native double[] nativeScreenToWorldPoint(long jniEegeoMapApiPtr, double x, double y);

}
