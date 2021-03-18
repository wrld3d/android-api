package com.eegeo.mapapi.services.routing;

import androidx.annotation.UiThread;


/**
 * A listener interface for receiving the results of a completed routing query.
 */
public interface OnRoutingQueryCompletedListener {

    /**
     * Called when a routing query completes.
     *
     * @param query The query this response is for.
     * @param response The response to the query. If successful, this will contain routing results.
     */
    @UiThread
    void onRoutingQueryCompleted(RoutingQuery query, RoutingQueryResponse response);

}
