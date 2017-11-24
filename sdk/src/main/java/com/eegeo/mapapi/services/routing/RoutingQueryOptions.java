package com.eegeo.mapapi.services.routing;

import java.util.List;
import java.util.ArrayList;
import com.eegeo.mapapi.geometry.LatLng;


/**
 * A set of parameters for a RoutingQuery.
 */
public final class RoutingQueryOptions {

    class Waypoint {

        public final LatLng latLng;
        public final boolean isIndoors;
        public final int floorNumber;

        public Waypoint(final LatLng latLng, boolean isIndoors, int floorNumber) {
            this.latLng = latLng;
            this.isIndoors = isIndoors;
            this.floorNumber = floorNumber;
        }
    }


    private List<Waypoint> m_waypoints = new ArrayList<Waypoint>();
    private OnRoutingQueryCompletedListener m_onRoutingQueryCompletedListener = null;


    /**
     * Add an outdoor waypoint to the route.
     *
     * @param latLng A LatLng this route should pass through.
     * @return This RoutingQueryOptions object.
     */
    public RoutingQueryOptions addWaypoint(LatLng latLng) {
        m_waypoints.add(new Waypoint(latLng, false, 0));
        return this;
    }

    /**
     * Add an indoor waypoint to the route.
     *
     * @param latLng A LatLng this route should pass through.
     * @param floorNumber The index of the floor this point lies on.
     * @return This RoutingQueryOptions object.
     */
    public RoutingQueryOptions addIndoorWaypoint(LatLng latLng, int floorNumber) {
        m_waypoints.add(new Waypoint(latLng, true, floorNumber));
        return this;
    }

    List<Waypoint> getWaypoints() {
        return m_waypoints;
    }

    /**
     * Sets a listener to receive routing results when the query completes.
     *
     * @param onPoiSearchCompletedListener A listener implementing the OnRoutingQueryCompletedListener interface.
     * @return This RoutingQueryOptions object.
     */
    public RoutingQueryOptions onRoutingQueryCompletedListener(OnRoutingQueryCompletedListener onRoutingQueryCompletedListener) {
        this.m_onRoutingQueryCompletedListener = onRoutingQueryCompletedListener;
        return this;
    }

    OnRoutingQueryCompletedListener getOnRoutingQueryCompletedListener() {
        return m_onRoutingQueryCompletedListener;
    }
}

