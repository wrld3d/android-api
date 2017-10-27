package com.eegeo.mapapi.services;


import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;


public class PoiSearchResult {

    public final int id;
    public final String title;
    public final String subtitle;
    public final String tags;
    public final LatLng latLng;
    public final double heightOffset;
    public final boolean indoor;
    public final String indoorId;
    public final int floorId;
    public final String userData;

    @UiThread
    PoiSearchResult(
            int id,
            String title,
            String subtitle,
            String tags,
            LatLng latLng,
            double heightOffset,
            boolean indoor,
            String indoorId,
            int floorId,
            String userData)
    {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.tags = tags;
        this.latLng = latLng;
        this.heightOffset = heightOffset;
        this.indoor = indoor;
        this.indoorId = indoorId;
        this.floorId = floorId;
        this.userData = userData;
    }
}

