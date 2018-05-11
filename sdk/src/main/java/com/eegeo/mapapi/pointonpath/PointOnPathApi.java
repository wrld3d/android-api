package com.eegeo.mapapi.pointonpath;

import java.util.concurrent.Callable;
import java.util.List;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.services.routing.Route;

public class PointOnPathApi {

    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;

    public PointOnPathApi(INativeMessageRunner nativeRunner, IUiMessageRunner uiRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }

    public PointOnRoute getPointOnRoute(LatLng point, Route route, PointOnRouteOptions pointOnRouteOptions)
    {
        return nativeGetPointOnRoute(m_jniEegeoMapApiPtr, point.latitude, point.longitude, route, pointOnRouteOptions);
    }

    // Most of our native functions are run on the @WorkerThread, but in this case the API points
    // that we're calling into do not require platform state - they are free functions that just
    // perform some calculations.
    @UiThread
    private native PointOnRoute nativeGetPointOnRoute(
            long jniEegeoMapApiPtr,
            double latitude,
            double longitude,
            Route routeData,
            PointOnRouteOptions pointOnRouteOptions);
}
