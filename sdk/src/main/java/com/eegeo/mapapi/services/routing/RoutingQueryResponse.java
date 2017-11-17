package com.eegeo.mapapi.services.routing;


/**
 * A response to a routing query. Returned when a routing query completes via callback.
 */
public class RoutingQueryResponse {

    boolean m_succeeded;
    RoutingResults m_results;


    RoutingQueryResponse(boolean succeeded, RoutingResults results) {
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
     * Get the results of the query as a List of RoutingQueryResult objects.
     * @return The query results.
     */
    public RoutingResults getResults() {
        return m_results;
    }
}

