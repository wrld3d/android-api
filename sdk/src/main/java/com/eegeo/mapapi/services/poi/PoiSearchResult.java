package com.eegeo.mapapi.services.poi;


import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLng;


/**
 * The data for a single POI, returned by a POI search.
 */
public class PoiSearchResult {

    /**
     * A unique ID for this POI.
     */
    public final int id;

    /**
     * The title text for this POI.
     */
    public final String title;

    /**
     * The subtitle text for this POI.
     */
    public final String subtitle;

    /**
     * A tag, or a space-separated list of tags, for this POI.
     */
    public final String tags;

    /**
     * The geographic location of this POI.
     */
    public final LatLng latLng;

    /**
     * The distance from the ground, in metres, of this POI.
     */
    public final double heightOffset;

    /**
     * Whether this POI is indoors or not.
     */
    public final boolean indoor;

    /**
     * The ID of the indoor map this POI is inside. If the POI is outdoors, this is the empty string.
     */
    public final String indoorId;

    /**
     * The floor number this POI is on. If the POI is outdoors, this defaults to 0.
     */
    public final int floorId;

    /**
     * Arbitrary JSON user data. This can be empty, or can be any JSON data this POI has associated.
     */
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

