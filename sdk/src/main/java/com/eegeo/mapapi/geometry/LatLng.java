package com.eegeo.mapapi.geometry;

/**
 * An immutable class representing a point on the earth's surface, expressed as a latitude and longitude in degrees.
 */
public class LatLng {
    /**
     * Latitude, in degrees.
     */
    public final double latitude;

    /**
     * Longitude, in degrees.
     */
    public final double longitude;

    /**
     * Creates a LatLng from values.
     *
     * @param latitude  Latitude, in degrees.
     * @param longitude Longitude, in degrees.
     */
    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
        LatLng latLng = (LatLng) (object);
        return Double.compare(latLng.latitude, latitude) == 0
                && Double.compare(latLng.longitude, longitude) == 0;
    }
}
