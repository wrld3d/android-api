package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Arrays;
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
            @NonNull IndoorFloorOutlinePolygonContour innerContours[]
    ) {
        this.outerContour = outerContour;
        this.innerContours = Arrays.asList(innerContours);
    }
}
