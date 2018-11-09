package com.eegeo.mapapi.indoorentities;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

/**
 * Represents infomation about an identifiable feature on an indoor map.
 * These correspond to features within a level GeoJSON in an indoor map submission via the WRLD Indoor Map REST API.
 * See [https://github.com/wrld3d/wrld-indoor-maps-api/blob/master/FORMAT.md](https://github.com/wrld3d/wrld-indoor-maps-api/blob/master/FORMAT.md)
 */
public class IndoorMapEntity
{
    /**
     * The string identifier of this indoor map entity.
     * This identifier is expected to be unique across all indoor map entities 
     * for a single indoor map. 
     */
    public final String indoorMapEntityId;

    /**
     * The identifier of an indoor map floor on which this indoor map entity is positioned.
     */
    public final int indoorMapFloorId;

    /**
     * The location of this indoor map entity. Although indoor map entities can represent area
     * features such as rooms or desks, this position provides a point that is in the center of the
     * feature. As such, it is suitable for use if locating a Marker for this entity, or if
     * positioning the camera to look at this entity.
     */
    public final LatLng position;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorMapEntity(
            @NonNull String indoorMapEntityId,
            @NonNull int indoorMapFloorId,
            @NonNull LatLng position

    ) {
        this.indoorMapEntityId = indoorMapEntityId;
        this.indoorMapFloorId = indoorMapFloorId;
        this.position = position;
    }

}


