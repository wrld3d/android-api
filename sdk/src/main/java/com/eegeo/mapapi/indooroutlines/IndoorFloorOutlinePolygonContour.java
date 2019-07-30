package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

import java.util.Arrays;
import java.util.List;

public class IndoorFloorOutlinePolygonContour {

    public final List<LatLng> contourPoints;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorFloorOutlinePolygonContour(@NonNull LatLng contourPoints[]) {
        this.contourPoints = Arrays.asList(contourPoints);
    }
}
