package com.eegeo.mapapi.picking;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLngAlt;
import com.eegeo.mapapi.geometry.MapFeatureType;
import com.eegeo.mapapi.geometry.Vector3;


public class PickResult {
    public final boolean found;
    public final MapFeatureType mapFeatureType;
    public final LatLngAlt intersectionPoint;
    public final Vector3 intersectionSurfaceNormal;
    public final String collisionMaterialId;


    @UiThread
    public PickResult(
            boolean found,
            @NonNull MapFeatureType mapFeatureType,
            @NonNull LatLngAlt intersectionPoint,
            @NonNull Vector3 intersectionSurfaceNormal,
            @NonNull String collisionMaterialId
    ) {
        this.found = found;
        this.mapFeatureType = mapFeatureType;
        this.intersectionPoint = intersectionPoint;
        this.intersectionSurfaceNormal = intersectionSurfaceNormal;
        this.collisionMaterialId = collisionMaterialId;
    }

}
