package com.eegeo.mapapi.services.routing;

import java.util.concurrent.Callable;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.eegeo.mapapi.util.NativeApiObject;


public class RoutingQuery extends NativeApiObject {

    private RoutingApi m_routingApi;

    @UiThread
    RoutingQuery(final RoutingApi routingApi, final RoutingQueryOptions options) {
        super(routingApi.getNativeRunner(), routingApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        Integer id = routingApi.beginRouteQuery(options);
                        Log.i("eegeo", "Searching for route: " + String.valueOf(id));
                        return id;
                    }
                });

        m_routingApi = routingApi;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_routingApi.register(RoutingQuery.this, getNativeHandle());
            }
        });
    }

//     @UiThread
//     public void cancel() {
//         submit(new Runnable() {
//             @WorkerThread
//             public void run() {
//                 m_poiApi.cancelSearch(getNativeHandle());
//             }
//         });
//     }

//     void returnSearchResults(PoiSearchResponse searchResults) {
//         if (m_callback != null) {
//             m_callback.onPoiSearchCompleted(searchResults);
//         }
//     }
}
