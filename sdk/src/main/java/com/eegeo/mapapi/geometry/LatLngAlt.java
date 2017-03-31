package com.eegeo.mapapi.geometry;

/**
 * An immutable class representing a point on the globe in polar coordinates, with latitude and longitude
 * in degrees, and absolute altitude above sea level in meters.
 */
public class LatLngAlt {
    /**
     * Latitude, in degrees.
     */
    public final double latitude;

    /**
     * Longitude, in degrees.
     */
    public final double longitude;

    /**
     * Height of point above sea level, in meters.
     */
    public final double altitude;

    /**
     * Creates a LatLngAlt from values.
     *
     * @param latitude  Latitude, in degrees.
     * @param longitude Longitude, in degrees.
     * @param altitude  Altitude, in meters.
     */
    public LatLngAlt(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    /**
     * Gets the position of the point ignoring altitude.
     *
     * @return The position of the point, ignoring altitude.
     */
    public final LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    /**
     * Equality operator.
     *
     * @param object The object to compare.
     * @return True if the objects are equal
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        LatLngAlt latLngAlt = (LatLngAlt) (object);
        return Double.compare(latLngAlt.latitude, latitude) == 0
                && Double.compare(latLngAlt.longitude, longitude) == 0
                && Double.compare(latLngAlt.altitude, altitude) == 0;
    }
}
