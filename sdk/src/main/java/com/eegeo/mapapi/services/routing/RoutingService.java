package com.eegeo.mapapi.services.routing;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;


public class RoutingService {

    private RoutingApi m_routingApi;

    public RoutingService(RoutingApi routingApi) {
        this.m_routingApi = routingApi;
    }

    @UiThread
    public RoutingQuery findRoutes(RoutingQueryOptions options) {
        return new RoutingQuery(m_routingApi, options);
    }
}

