package com.eegeo.mapapi.picking;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLngAlt;
import com.eegeo.mapapi.geometry.MapFeatureType;
import com.eegeo.mapapi.geometry.Vector3;

/**
 * Result values returned by EegeoMap picking api methods.
 */
public class PickResult {
    /**
     * True if the picking ray intersected with a map feature, else false.
     */
    public final boolean found;

    /**
     * The type of map feature intersected with, if any.
     */
    public final MapFeatureType mapFeatureType;

    /**
     * The location of intersection, if any.
     */
    public final LatLngAlt intersectionPoint;

    /**
     * The surface normal of the map feature intersected with, if any, in ECEF coordinates.
     */
    public final Vector3 intersectionSurfaceNormal;

    /**
     * @eegeo.internal
     */
    public final String collisionMaterialId;

    /**
     * @eegeo.internal
     */
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
