package com.eegeo.mapapi.camera;

import android.content.res.TypedArray;
import android.support.annotation.NonNull;

import com.eegeo.mapapi.R;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.LatLngAlt;

/**
 * Class which encapsulates a 3D camera position and orientation.
 */
public final class CameraPosition {
    /**
     * The position of the camera.
     */
    public final LatLngAlt target;
    /**
     * The zoom level, in the range 0 to 26.
     */
    public final double zoom;
    /**
     * The camera tilt in degrees, as an offset from earth normal.
     */
    public final double tilt;
    /**
     * The camera bearing in degrees, as an offset from north.
     */
    public final double bearing;

    /**
     * Distance from camera to target.
     *
     * @eegeo.internal
     */
    public final double distance;
    /**
     * Flag to signify that the target has changed.
     *
     * @eegeo.internal
     */
    public final boolean modifyTarget;
    /**
     * Flag to signify that the distance has changed.
     *
     * @eegeo.internal
     */
    public final boolean modifyDistance;
    /**
     * Flag to signify that the bearing has changed.
     *
     * @eegeo.internal
     */
    public final boolean modifyBearing;
    /**
     * Flag to signify that the tilt has changed.
     *
     * @eegeo.internal
     */
    public final boolean modifyTilt;


    /**
     * @param target  The target point.
     * @param zoom    Zoom level.
     * @param tilt    Camera tilt in degrees. Tilt is an offset from the earth normal, so a tilt of 0 means the camera looks straight down at the earth.
     *                The SDK will clamp the requested tilt to be within limits dependent on altitude.
     * @param bearing Orientation of the camera in the earth tangent plane, in degrees clockwise from north.
     * @eegeo.internal Create a camera position with the given parameters.
     */
    public CameraPosition(LatLngAlt target, double zoom, double tilt, double bearing) {
        this.target = target;
        this.zoom = zoom;
        this.tilt = tilt;
        this.bearing = bearing;

        this.distance = Builder.ZoomToDistance(zoom);
        this.modifyTarget = true;
        this.modifyDistance = true;
        this.modifyBearing = true;
        this.modifyTilt = true;
    }

    /**
     * @eegeo.internal Constructor used by the builder.
     */

    private CameraPosition(LatLngAlt target,
                           boolean modifyTarget,
                           double distance,
                           boolean modifyDistance,
                           double bearing,
                           boolean modifyBearing,
                           double tilt,
                           boolean modifyTilt) {
        this.target = target;
        this.tilt = tilt;
        this.bearing = bearing;
        this.zoom = Builder.DistanceToZoom(distance);

        this.distance = distance;
        this.modifyTarget = modifyTarget;
        this.modifyDistance = modifyDistance;
        this.modifyBearing = modifyBearing;
        this.modifyTilt = modifyTilt;
    }

    /**
     * Builds a camera position.
     */
    public static final class Builder {
        private static double[] ms_zoomToDistances = new double[]{27428700,
                14720762,
                8000000,
                4512909,
                2087317,
                1248854,
                660556,
                351205,
                185652,
                83092,
                41899,
                21377,
                11294,
                5818,
                3106,
                1890,
                1300,
                821,
                500,
                300,
                108,
                58,
                31,
                17,
                9,
                5};
        private LatLngAlt m_target;
        private double m_distance = 0.0;
        private double m_tilt = 0.0;
        private double m_bearing = 0.0;
        private boolean m_modifyTarget = false;
        private boolean m_modifyTilt = false;
        private boolean m_modifyBearing = false;
        private boolean m_modifyDistance = false;

        /**
         * Creates an empty builder.
         */
        public Builder() {
            super();
        }

        /**
         * Creates a builder and populates it with resource values.
         *
         * @param typedArray
         * @eegeo.internal
         */
        public Builder(@NonNull TypedArray typedArray) {
            super();

            double lat = typedArray.getFloat(R.styleable.eegeo_MapView_camera_target_latitude, 0.0f);
            double lng = typedArray.getFloat(R.styleable.eegeo_MapView_camera_target_longitude, 0.0f);
            double altitude = typedArray.getFloat(R.styleable.eegeo_MapView_camera_target_altitude, 0.0f);

            target(lat, lng, altitude);
            // camera_distance attribute takes precedence over zoom
            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_distance)) {
                double distance = typedArray.getFloat(R.styleable.eegeo_MapView_camera_distance, 0.0f);
                distance(distance);
            } else if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_zoom)) {
                double zoom = typedArray.getFloat(R.styleable.eegeo_MapView_camera_zoom, 0.0f);
                zoom(zoom);
            }

            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_bearing)) {
                double bearing = typedArray.getFloat(R.styleable.eegeo_MapView_camera_bearing, 0.0f);
                bearing(bearing);
            }

            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_tilt)) {
                double tilt = typedArray.getFloat(R.styleable.eegeo_MapView_camera_tilt, 0.0f);
                tilt(tilt);
            }

        }

        /**
         * Convert a zoom to a distance value
         *
         * @param zoom
         * @return Distance in meters.
         * @eegeo.internal
         */
        public static double ZoomToDistance(double zoom) {
            int zoomLevel = (int) zoom;
            if (zoomLevel < 0) {
                return ms_zoomToDistances[0];
            }

            if (zoomLevel >= ms_zoomToDistances.length) {
                return ms_zoomToDistances[ms_zoomToDistances.length - 1];
            }

            return ms_zoomToDistances[zoomLevel];
        }

        /**
         * Convert a distance to an approximate zoom.
         *
         * @param distance
         * @return The approximate zoom.
         * @eegeo.internal
         */
        public static double DistanceToZoom(double distance) {
            for (int i = 0; i < ms_zoomToDistances.length; ++i) {
                if (distance >= ms_zoomToDistances[i]) {
                    return i;
                }
            }
            return 17;
        }

        /**
         * Sets the orientation of the camera in the earth tangent plane, in degrees clockwise from north.
         *
         * @param bearing Angle in degrees.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder bearing(double bearing) {
            while (bearing >= 360.0) {
                bearing -= 360.0;
            }
            while (bearing < 0.0) {
                bearing += 360.0;
            }
            this.m_bearing = bearing;
            this.m_modifyBearing = true;
            return this;
        }

        /**
         * Sets the target point of the camera.
         *
         * @param latitude  Latitude in degrees.
         * @param longitude Longitude in degrees.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder target(double latitude, double longitude) {
            this.m_target = new LatLngAlt(latitude, longitude, 0.0);
            this.m_modifyTarget = true;
            return this;
        }

        /**
         * Sets the target point of the camera.
         *
         * @param latitude  Latitude in degrees.
         * @param longitude Longitude in degrees.
         * @param altitude  Altitude in meters.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder target(double latitude, double longitude, double altitude) {
            this.m_target = new LatLngAlt(latitude, longitude, altitude);
            this.m_modifyTarget = true;
            return this;
        }

        /**
         * Sets the target point of the camera.
         *
         * @param latLon Target point.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder target(@NonNull LatLng latLon) {
            this.m_target = new LatLngAlt(latLon.latitude, latLon.longitude, 0.0);
            this.m_modifyTarget = true;
            return this;
        }

        /**
         * Sets the target point of the camera.
         *
         * @param latLonAlt Target point.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder target(@NonNull LatLngAlt latLonAlt) {
            this.m_target = latLonAlt;
            this.m_modifyTarget = true;
            return this;
        }

        /**
         * Sets the distance.
         *
         * @param distance
         * @return Updated CameraPosition.Builder object.
         * @eegeo.internal
         */
        private Builder distance(double distance) {
            this.m_distance = distance; // todo - clamp?
            this.m_modifyDistance = true;
            return this;
        }

        /**
         * Sets the camera zoom.
         *
         * @param zoom The new zoom value.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder zoom(double zoom) {
            distance(ZoomToDistance(zoom));
            return this;
        }

        /**
         * Sets the tilt of the camera.
         *
         * @param tilt Camera tilt in degrees. Tilt is an offset from the earth normal, so a tilt of 0 means the camera looks straight down at the earth.
         *             The SDK will clamp the requested tilt to be within limits dependent on altitude.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder tilt(double tilt) {
            this.m_tilt = Math.min(Math.max(tilt, 0.0), 90.0);
            this.m_modifyTilt = true;
            return this;
        }

        /**
         * Builds a CameraPosition object.
         *
         * @return The final CameraPosition object.
         */
        public final CameraPosition build() {
            return new CameraPosition(m_target, m_modifyTarget,
                    m_distance, m_modifyDistance,
                    m_bearing, m_modifyBearing,
                    m_tilt, m_modifyTilt);
        }
    }
}
