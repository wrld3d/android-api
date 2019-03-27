package com.eegeo.mapapi.geometry;

public class WeightedLatLngAlt {

    public static final double DEFAULT_INTENSITY = 1.0;

    public final LatLngAlt point;

    public final double intensity;

    public WeightedLatLngAlt(final double latitude, final double longitude) {
        this.point = new LatLngAlt(latitude, longitude, 0.0);
        this.intensity = DEFAULT_INTENSITY;
    }

    public WeightedLatLngAlt(final double latitude, final double longitude, double intensity) {
        this.point = new LatLngAlt(latitude, longitude, 0.0);
        this.intensity = intensity;
    }

    public WeightedLatLngAlt(final LatLngAlt point) {
        this.point = point;
        this.intensity = DEFAULT_INTENSITY;
    }

    public WeightedLatLngAlt(final LatLngAlt point, double intensity) {
        this.point = point;
        this.intensity = intensity;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        WeightedLatLngAlt other = (WeightedLatLngAlt)(object);
        if (Double.compare(this.intensity, other.intensity) == 0) {
            return false;
        }
        return this.point.equals(other.point);
    }

}
