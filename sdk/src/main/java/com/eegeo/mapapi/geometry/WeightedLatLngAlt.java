package com.eegeo.mapapi.geometry;

/**
 * A wrapper class to associate a scale value with a LatLngAlt.
 */
public class WeightedLatLngAlt {

    /**
     * Default value of intensity field.
     */
    public static final double DEFAULT_INTENSITY = 1.0;

    /**
     * The point coordinate.
     */
    public final LatLngAlt point;

    /**
     * The scalar weight value.
     */
    public final double intensity;

    /**
     * Create from coordinates with default altitude and intensity.
     *
     * @param latitude Latitude, in degrees.
     * @param longitude Longitude, in degrees.
     */
    public WeightedLatLngAlt(final double latitude, final double longitude) {
        this.point = new LatLngAlt(latitude, longitude, 0.0);
        this.intensity = DEFAULT_INTENSITY;
    }

    /**
     * Create from coordinates with default altitude.
     *
     * @param latitude Latitude, in degrees.
     * @param longitude Longitude, in degrees.
     * @param intensity Scalar weight value, in degrees.
     */
    public WeightedLatLngAlt(final double latitude, final double longitude, double intensity) {
        this.point = new LatLngAlt(latitude, longitude, 0.0);
        this.intensity = intensity;
    }

    /**
     * Create from LatLngAlt object with default intensity.
     *
     * @param point The coordinates object.
     */
    public WeightedLatLngAlt(final LatLngAlt point) {
        this.point = point;
        this.intensity = DEFAULT_INTENSITY;
    }

    public WeightedLatLngAlt(final LatLngAlt point, double intensity) {
        this.point = point;
        this.intensity = intensity;
    }

    /**
     * Equality comparison.
     *
     * @param object The object instance to compare with this.
     * @return True if objects are equal.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        WeightedLatLngAlt other = (WeightedLatLngAlt)(object);
        if (Double.compare(this.intensity, other.intensity) != 0) {
            return false;
        }
        return this.point.equals(other.point);
    }

}
