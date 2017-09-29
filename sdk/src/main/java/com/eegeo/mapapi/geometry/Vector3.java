package com.eegeo.mapapi.geometry;

/**
 * Am immutable 3-element, double-precision vector
 */
public class Vector3 {
    /**
     * The x coordinate
     */
    public final double x;

    /**
     * The x coordinate
     */
    public final double y;

    /**
     * The z coordinate
     */
    public final double z;

    /**
     * Creates a Vector3 from coordinate values.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
        Vector3 v = (Vector3) (object);
        return ((Double.compare(v.x, x) == 0) && (Double.compare(v.y, y) == 0) && (Double.compare(v.z, z) == 0));
    }
}
