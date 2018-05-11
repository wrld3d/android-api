package com.eegeo.mapapi.pointonpath;

import android.support.annotation.UiThread;

public final class PointOnRouteOptions {

    public final String indoorMapId;

    public final int indoorMapFloorId;

    public PointOnRouteOptions(
            String indoorMapId,
            int indoorMapFloorId
    ) {
        this.indoorMapId = indoorMapId;
        this.indoorMapFloorId = indoorMapFloorId;
    }

    public PointOnRouteOptions(
    ) {
        this.indoorMapId = "";
        this.indoorMapFloorId = 0;
    }

}
