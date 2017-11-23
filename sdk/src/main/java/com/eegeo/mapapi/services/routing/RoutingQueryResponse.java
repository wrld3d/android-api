package com.eegeo.mapapi.services.routing;

import java.util.List;


/**
 * A response to a routing query. Returned when a routing query completes via callback.
 */
public class RoutingQueryResponse {

    boolean m_succeeded;
    List<Route> m_results;


    RoutingQueryResponse(boolean succeeded, List<Route> results) {
        this.m_succeeded = succeeded;
        this.m_results = results;
    }

    /**
     * @return A boolean indicating whether the search succeeded or not.
     */
    public boolean succeeded() {
        return m_succeeded;
    }

    /**
     * Get the results of the query as a List of Routes
     * @return The query results.
     */
    public List<Route> getResults() {
        return m_results;
    }
}

