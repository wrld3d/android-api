package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.List;

public class IndoorFloorOutlinePolygon {
    public final IndoorFloorOutlinePolygonContour outerContour;
    public final List<IndoorFloorOutlinePolygonContour> innerContours;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorFloorOutlinePolygon(
            @NonNull IndoorFloorOutlinePolygonContour outerContour,
            @NonNull List<IndoorFloorOutlinePolygonContour> innerContours
    ) {
        this.outerContour = outerContour;
        this.innerContours = innerContours;
    }
}
