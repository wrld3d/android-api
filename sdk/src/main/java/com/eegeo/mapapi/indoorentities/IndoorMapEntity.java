package com.eegeo.mapapi.indoorentities;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

/**
 * Represents infomation about an identifiable feature on an indoor map.
 * These correspond to features within a level GeoJSON in an indoor map submission via the WRLD Indoor Map REST API.
 * See https://github.com/wrld3d/wrld-indoor-maps-api/blob/master/FORMAT.md
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
     * @eegeo.internal
     */
    @UiThread
    public IndoorMapEntity(
            @NonNull String indoorMapEntityId,
            @NonNull int indoorMapFloorId
    ) {
        this.indoorMapEntityId = indoorMapEntityId;
        this.indoorMapFloorId = indoorMapFloorId;
    }

}


