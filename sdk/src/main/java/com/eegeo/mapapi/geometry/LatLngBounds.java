package com.eegeo.mapapi.geometry;

import androidx.annotation.NonNull;

/**
 * An immutable class representing an area of the earth's surface with a latitude/longitude aligned rectangle.
 */
public class LatLngBounds {
    /**
     * The coordinate of the Southwest corner of the bound.
     */
    public final LatLng southwest;

    /**
     * The coordinate of the Northeast corner of the bound.
     */
    public final LatLng northeast;

    /**
     * Creates a new bounds based on the southwest and northeast corners.
     *
     * @param southwest The coordinates of the southwest corner.
     * @param northeast The coordinates of the northeast corner.
     * @throws IllegalArgumentException If the northeast latitude is less than the southwest latitude.
     */
    public LatLngBounds(@NonNull LatLng southwest, @NonNull LatLng northeast) throws IllegalArgumentException {
        if (northeast.latitude < southwest.latitude) {
            throw new IllegalArgumentException();
        }
        this.southwest = southwest;
        this.northeast = northeast;
    }

    /**
     * Returns a new LatLngBounds that encompasses the this LatLngBounds and the given point.
     *
     * @param point a LatLng to extend this bounds by.
     * @return a LatLngBounds that contains point and the previous bounds.
     */
    public LatLngBounds including(@NonNull LatLng point) {

        return new LatLngBounds(
                new LatLng(Math.min(this.southwest.latitude, point.latitude),
                        Math.min(this.southwest.longitude, point.longitude)),
                new LatLng(Math.max(this.northeast.latitude, point.latitude),
                        Math.max(this.northeast.longitude, point.longitude))
        );
    }

    /**
     * Equality operator.
     *
     * @param other The other to compare.
     * @return True if the objects are equal
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LatLngBounds llb = (LatLngBounds) (other);
        return llb.southwest.equals(this.southwest) &&
                llb.northeast.equals(this.northeast);
    }


    /**
     * A builder class that can create a LatLngBounds from one or more LatLng points.
     */
    public static final class Builder {

        private LatLngBounds m_bounds;

        public Builder() {
        }

        /**
         * Include point in set of points to build bounds for.
         *
         * @param point A LatLng point to be included in the bounds.
         * @return This builder object with a new point added.
         */
        public Builder include(@NonNull LatLng point) {
            if (m_bounds == null) {
                m_bounds = new LatLngBounds(point, point);
            } else {
                m_bounds = m_bounds.including(point);
            }
            return this;
        }

        /**
         * Generate a LatLngBounds encompassing the points.
         *
         * @return a LatLngBounds that minimally encompasses all previously included points.
         * @throws IllegalStateException if no point has been previously included.
         */
        public LatLngBounds build() {
            if (m_bounds == null) {
                throw new IllegalStateException("unable to build LatLngBounds from empty list of points");
            }
            return m_bounds;
        }
    }

}
