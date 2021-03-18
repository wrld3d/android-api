package com.eegeo.mapapi.paths;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.services.routing.Route;
import com.eegeo.mapapi.util.Promise;
import com.eegeo.mapapi.geometry.LatLngHelpers;

import java.util.List;

public class PathApi {

    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;

    public PathApi(INativeMessageRunner nativeRunner, IUiMessageRunner uiRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    @UiThread
    public Promise<PointOnRoute> getPointOnRoute(final LatLng point, final Route route, final PointOnRouteOptions pointOnRouteOptions)
    {
        final Promise<PointOnRoute> p = new Promise<>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final PointOnRoute pointOnRoute = nativeGetPointOnRoute(m_jniEegeoMapApiPtr, point.latitude, point.longitude, route, pointOnRouteOptions);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(pointOnRoute);
                    }
                });
            }
        });
        return p;
    }

    @WorkerThread
    private native PointOnRoute nativeGetPointOnRoute(
            long jniEegeoMapApiPtr,
            double latitude,
            double longitude,
            Route routeData,
            PointOnRouteOptions pointOnRouteOptions);


    @UiThread
    public Promise<PointOnPath> getPointOnPath(final LatLng point, final List<LatLng> path)
    {
        final Promise<PointOnPath> p = new Promise<>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                double[] latLongs = LatLngHelpers.pointsToArray(path);
                final PointOnPath pointOnPath = nativeGetPointOnPath(m_jniEegeoMapApiPtr, point.latitude, point.longitude, latLongs);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(pointOnPath);
                    }
                });
            }
        });
        return p;
    }

    @WorkerThread
    private native PointOnPath nativeGetPointOnPath(
            long jniEegeoMapApiPtr,
            double latitude,
            double longitude,
            double[] path);


}
