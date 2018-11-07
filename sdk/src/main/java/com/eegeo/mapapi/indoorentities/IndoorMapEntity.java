package com.eegeo.mapapi.indoorentities;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;


public class IndoorMapEntity
{
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
            @NonNull String indoorMapEntityId,
            @NonNull int indoorMapFloorId
    ) {
        this.indoorMapEntityId = indoorMapEntityId;
        this.indoorMapFloorId = indoorMapFloorId;
    }

}


