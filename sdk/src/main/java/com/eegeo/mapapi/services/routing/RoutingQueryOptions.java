package com.eegeo.mapapi.services.routing;

import java.util.List;
import java.util.ArrayList;
import com.eegeo.mapapi.geometry.LatLng;


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
//     private OnPoiSearchCompletedListener m_onPoiSearchCompletedListener = null;

    public RoutingQueryOptions addWaypoint(LatLng latLng) {
        m_waypoints.add(new Waypoint(latLng, false, 0));
        return this;
    }

    public RoutingQueryOptions addIndoorWaypoint(LatLng latLng, int floorNumber) {
        m_waypoints.add(new Waypoint(latLng, true, floorNumber));
        return this;
    }

    List<Waypoint> getWaypoints() {
        return m_waypoints;
    }

//     public RoutingQueryOptions onPoiSearchCompletedListener(OnPoiSearchCompletedListener onPoiSearchCompletedListener) {
//         this.m_onPoiSearchCompletedListener = onPoiSearchCompletedListener;
//         return this;
//     }
//
//     OnPoiSearchCompletedListener getOnPoiSearchCompletedListener() {
//         return m_onPoiSearchCompletedListener;
//     }
}

