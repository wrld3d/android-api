package com.eegeo.mapapi.services.routing;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;


/**
 * A service which allows you to find routes between locations. Created by the createRoutingService method of the EegeoMap object.
 *
 * This is a Java interface to the WRLD Routing REST API (https://github.com/wrld3d/wrld-routing-api).
 */
public class RoutingService {

    private RoutingApi m_routingApi;

    /**
     * @eegeo.internal
     */
    public RoutingService(RoutingApi routingApi) {
        this.m_routingApi = routingApi;
    }

    /**
     * Asynchronously query the routing service.
     *
     * The results of the query will be passed as a RoutingResults object to the callback provided in the options.
     *
     * @param options The parameters of the routing query.
     */
    @UiThread
    public RoutingQuery findRoutes(RoutingQueryOptions options) {
        return new RoutingQuery(m_routingApi, options);
    }
}

