package com.eegeo.mapapi.services.mapscene;

import android.support.annotation.UiThread;

import com.eegeo.mapapi.geometry.LatLngAlt;


/**
 * The data for representing the start location of a Mapscene. Includes starting orientation and
 * optional Indoor Map configuration.
 */
public class MapsceneStartLocation
{
    /**
     * The initial Latitude, Longitude and Altitude of the starting camera's view.
     */
    public final LatLngAlt startLocation;

    /**
     * The initial bearing in degrees, as an offset from north.
     */
    public final float startLocationBearing;

    /**
     * The initial distance between the camera's position and its focused interest position.
     */
    public final float startLocationDistanceToInterest;

    /**
     * An optional ID for an Indoor Map to start in - Default is blank/none.
     */
    public final String startLocationIndoorMapId;

    /**
     * An optional Floor Index to start in - Default is 0. Only applicable when setting an
     * Indoor Map id as well.
     */
    public final int startLocationIndoorMapFloorIndex;

    /**
     * An optional flag to specify if you want to try starting the map at the Device's GPS
     * position.  Only currently applicable to mobile devices via the Wrld App.
     */
    public final boolean tryStartAtGpsLocation;

    @UiThread
    MapsceneStartLocation(
            LatLngAlt startLocation,
            float bearing,
            float distanceToInterest,
            String indoorMapId,
            int indoorMapFloorIndex,
            boolean tryStartAtGpsLocation)
    {
        this.startLocation = startLocation;
        this.startLocationBearing = bearing;
        this.startLocationDistanceToInterest = distanceToInterest;
        this.startLocationIndoorMapId = indoorMapId;
        this.startLocationIndoorMapFloorIndex = indoorMapFloorIndex;
        this.tryStartAtGpsLocation = tryStartAtGpsLocation;
    }
}
