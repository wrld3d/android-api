package com.eegeo.mapapi.services.routing;

import java.util.concurrent.Callable;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.eegeo.mapapi.util.NativeApiObject;


/**
 * A handle to an ongoing routing service query.
 */
public class RoutingQuery extends NativeApiObject {

    private RoutingApi m_routingApi;
    private OnRoutingQueryCompletedListener m_callback;

    @UiThread
    RoutingQuery(final RoutingApi routingApi, final RoutingQueryOptions options) {
        super(routingApi.getNativeRunner(), routingApi.getUiRunner(),
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        Integer id = routingApi.beginRouteQuery(options);
                        return id;
                    }
                });

        m_routingApi = routingApi;
        m_callback = options.getOnRoutingQueryCompletedListener();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_routingApi.register(RoutingQuery.this, getNativeHandle());
            }
        });
    }

    /**
     * Cancels the current query if it has not yet been completed.
     */
    @UiThread
    public void cancel() {
        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_routingApi.cancelQuery(getNativeHandle());
            }
        });
    }

    @UiThread
    void returnQueryResponse(RoutingQueryResponse response) {
        if (m_callback != null) {
            m_callback.onRoutingQueryCompleted(this, response);
        }
    }
}
