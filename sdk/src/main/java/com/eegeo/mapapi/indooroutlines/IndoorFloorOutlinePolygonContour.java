package com.eegeo.mapapi.indooroutlines;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;

import java.util.List;

public class IndoorFloorOutlinePolygonContour {

    public final List<LatLng> contourPoints;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorFloorOutlinePolygonContour(@NonNull List<LatLng> contourPoints) {
        this.contourPoints = contourPoints;
    }
}
