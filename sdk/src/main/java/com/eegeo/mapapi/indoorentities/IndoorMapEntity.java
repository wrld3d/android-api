package com.eegeo.mapapi.indoorentities;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;


public class IndoorMapEntity
{
    /**
     *
     */
    public final String indoorMapId;


    /**
     *
     */
    public final String indoorMapEntityId;

    /**
     *
     */
    public final int indoorMapFloorId;

    /**
     *
     */

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorMapEntity(
            @NonNull String indoorMapId,
            @NonNull String indoorMapEntityId,
            @NonNull int indoorMapFloorId
            // Type?
    ) {
        this.indoorMapId = indoorMapId;
        this.indoorMapEntityId = indoorMapEntityId;
        this.indoorMapFloorId = indoorMapFloorId;
    }

}


