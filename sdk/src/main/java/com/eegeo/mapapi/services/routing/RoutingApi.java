package com.eegeo.mapapi.services.routing;

import java.util.concurrent.Callable;
import java.util.List;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.geometry.LatLng;


public class RoutingApi {

    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private SparseArray<RoutingQuery> m_nativeHandleToRoutingQuery = new SparseArray<>();


    public RoutingApi(INativeMessageRunner nativeRunner, IUiMessageRunner uiRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
    }


    @WorkerThread
    int beginRouteQuery(final RoutingQueryOptions options) {
        List<RoutingQueryOptions.Waypoint> waypoints = options.getWaypoints();
        int count = waypoints.size();

        double[] latitudes = new double[count];
        double[] longitudes = new double[count];
        boolean[] isIndoors = new boolean[count];
        int[] floorNumbers = new int[count];

        for (int i=0; i<count; ++i) {
            latitudes[i] = waypoints.get(i).latLng.latitude;
            longitudes[i] = waypoints.get(i).latLng.longitude;
            isIndoors[i] = waypoints.get(i).isIndoors;
            floorNumbers[i] = waypoints.get(i).floorNumber;
        }

        return nativeFindRoute(m_jniEegeoMapApiPtr, count, latitudes, longitudes, isIndoors, floorNumbers);
    }


    @WorkerThread
    void register(RoutingQuery routingQuery, int nativeHandle) {
        m_nativeHandleToRoutingQuery.put(nativeHandle, routingQuery);
    }

    @WorkerThread
    void unregister(int nativeHandle) {
        m_nativeHandleToRoutingQuery.remove(nativeHandle);
    }
// 
// 
//     @WorkerThread
//     public void notifySearchComplete(final int nativeHandle, final boolean succeeded, final List<PoiSearchResult> searchResults) {
//         PoiSearchResponse result = new PoiSearchResponse(succeeded, searchResults);
//         if (m_nativeHandleToPoiSearch.get(nativeHandle) != null) {
//             returnSearchResults(nativeHandle, result);
//         }
//     }
// 
//     @WorkerThread
//     void returnSearchResults(final int nativeHandle, final PoiSearchResponse searchResults) {
//         final PoiSearch poiSearch = m_nativeHandleToPoiSearch.get(nativeHandle);
// 
//         if (poiSearch == null)
//             throw new NullPointerException("PoiSearch object not found for nativeHandle");
// 
//         poiSearch.returnSearchResults(searchResults);
//         m_nativeHandleToPoiSearch.remove(nativeHandle);
//     }


    @UiThread
    INativeMessageRunner getNativeRunner() {
        return m_nativeRunner;
    }

    @UiThread
    IUiMessageRunner getUiRunner() {
        return m_uiRunner;
    }

    
    @WorkerThread
    private native int nativeFindRoute(
            long jniEegeoMapApiPtr,
            int waypointCount,
            double[] latitudes,
            double[] longitudes,
            boolean[] isIndoors,
            int[] floorNumbers);
}

