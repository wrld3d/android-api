package com.eegeo.mapapi.camera;

import android.content.res.TypedArray;
import android.support.annotation.NonNull;

import com.eegeo.mapapi.R;
import com.eegeo.mapapi.geometry.ElevationMode;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.LatLngAlt;
import com.eegeo.mapapi.positioner.PositionerOptions;

/**
 * Class which encapsulates a 3D camera position and orientation.
 */
public final class CameraPosition {
    /**
     * The coordinate of the interest point that the camera is pointing at.
     */
    public final LatLng target;

    /**
     * The elevation of the interest point, in meters. The property is interpreted differently,
     * depending on targetElevationMode.
     */
    public final double targetElevation;

    /**
     * An enumerated value indicating whether targetElevation is specified as a height above
     * terrain, or an absolute altitude above sea level.
     */
    public final ElevationMode targetElevationMode;

    /**
     * For a camera with interest point on an indoor map, the string identifier of the indoor map;
     * else, an empty string.
     */
    public final String targetIndoorMapId;

    /**
     * For a camera with interest point on an indoor map, the identifier of an indoor map floor on
     * which the target interest point lies.
     */
    public final int targetIndoorMapFloorId;

    /**
     * For a camera with interest point on an indoor map, whether to target the default floor or not.
     */
    public final boolean targetIndoorMapDefaultFloor;

    /**
     * The zoom level, in the range 0 to 26.
     */
    public final double zoom;

    /**
     * The angle formed at the target coordinate from the camera direction to the vertical, in degrees.
     */
    public final double tilt;

    /**
     * The camera bearing, in degrees clockwise from north.
     */
    public final double bearing;


    /**
     * @param target  The target point.
     * @param zoom    Zoom level.
     * @param tilt    Camera tilt in degrees. Tilt is an offset from the earth normal, so a tilt of 0 means the camera looks straight down at the earth.
     *                The SDK will clamp the requested tilt to be within limits dependent on altitude.
     * @param bearing Orientation of the camera in the earth tangent plane, in degrees clockwise from north.
     * @eegeo.internal Create a camera position with the given parameters.
     */
    public CameraPosition(LatLng target, double zoom, double tilt, double bearing) {
        if (target == null) {
            throw new NullPointerException("A non-null target LatLng must be specified");
        }

        this.target = target;
        this.targetElevation = 0.0;
        this.targetElevationMode = ElevationMode.HeightAboveGround;
        this.targetIndoorMapId = "";
        this.targetIndoorMapFloorId = 0;
        this.targetIndoorMapDefaultFloor = false;

        this.zoom = zoom;
        this.tilt = tilt;
        this.bearing = bearing;
    }


    /**
     * @eegeo.internal Constructor used by the builder.
     */

    private CameraPosition(LatLng target,
                           double targetElevation,
                           ElevationMode targetElevationMode,
                           String targetIndoorMapId,
                           int targetIndoorMapFloorId,
                           boolean targetIndoorMapDefaultFloor,
                           double zoom,
                           double tilt,
                           double bearing) {
        if (target == null) {
            throw new NullPointerException("A non-null target LatLng must be specified");
        }
        this.target = target;
        this.targetElevation = targetElevation;
        this.targetElevationMode = targetElevationMode;
        this.targetIndoorMapId = targetIndoorMapId;
        this.targetIndoorMapFloorId = targetIndoorMapFloorId;
        this.targetIndoorMapDefaultFloor = targetIndoorMapDefaultFloor;

        this.zoom = zoom;
        this.tilt = tilt;
        this.bearing = bearing;
    }

    /**
     * Builds a CameraPosition.
     */
    public static final class Builder {
        private static double ms_defaultZoom = 17;
        private static double ms_defaultTilt = 45;
        private static double[] ms_zoomToDistances = new double[]{
                27428700,
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
                5
        };

        private LatLng m_target = null;
        private double m_targetElevation = 0.0;
        private ElevationMode m_targetElevationMode = ElevationMode.HeightAboveGround;
        private String m_targetIndoorMapId = "";
        private int m_targetIndoorMapFloorId = 0;
        private boolean m_targetIndoorMapDefaultFloor = false;

        private double m_zoom = ms_defaultZoom;
        private double m_tilt = ms_defaultTilt;
        private double m_bearing = 0.0;

        /**
         * Creates an empty builder.
         */
        public Builder() {
            super();
        }

        /**
         * Creates an builder initialised to the values of the supplied CameraPosition
         */
        public Builder(CameraPosition previous) {
            this.m_target = previous.target;
            this.m_targetElevation = previous.targetElevation;
            this.m_targetElevationMode = previous.targetElevationMode;
            this.m_targetIndoorMapId = previous.targetIndoorMapId;
            this.m_targetIndoorMapFloorId = previous.targetIndoorMapFloorId;
            this.m_targetIndoorMapDefaultFloor = previous.targetIndoorMapDefaultFloor;
            this.m_zoom = previous.zoom;
            this.m_tilt = previous.tilt;
            this.m_bearing = previous.bearing;
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

            target(lat, lng);

            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_target_altitude)) {
                double altitude = typedArray.getFloat(R.styleable.eegeo_MapView_camera_target_altitude, 0.0f);
                elevationMode(ElevationMode.HeightAboveSeaLevel);
                elevation(altitude);
            }

            // camera_distance attribute takes precedence over zoom
            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_distance)) {
                double distance = typedArray.getFloat(R.styleable.eegeo_MapView_camera_distance, 0.0f);
                distance(distance);
            } else if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_zoom)) {
                double zoom = typedArray.getFloat(R.styleable.eegeo_MapView_camera_zoom, 0.0f);
                zoom(zoom);
            }

            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_tilt)) {
                double tilt = typedArray.getFloat(R.styleable.eegeo_MapView_camera_tilt, 0.0f);
                tilt(tilt);
            }

            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_bearing)) {
                double bearing = typedArray.getFloat(R.styleable.eegeo_MapView_camera_bearing, 0.0f);
                bearing(bearing);
            }

            if (typedArray.hasValue(R.styleable.eegeo_MapView_camera_indoor_map_id) &&
                    typedArray.hasValue(R.styleable.eegeo_MapView_camera_indoor_map_floor_id)) {
                String indoorMapId = typedArray.getString(R.styleable.eegeo_MapView_camera_indoor_map_id);
                int indoorMapFloorId = typedArray.getInt(R.styleable.eegeo_MapView_camera_indoor_map_floor_id, 0);
                indoor(indoorMapId, indoorMapFloorId);
            }

        }

        /**
         * Convert a distance to a zoom level.
         *
         * @param distance Distance from camera to interest point in meters.
         * @return The equivalent zoom level.
         * @eegeo.internal
         */
        private static double DistanceToZoom(double distance) {
            int i1 = FirstZoomLevelLessThanDistance(distance);
            if (i1 < 0)
            {
                return ms_zoomToDistances.length - 1;
            }
            if (i1 == 0)
            {
                return i1;
            }
            int i0 = i1 - 1;

            double a = ms_zoomToDistances[i0];
            double b = ms_zoomToDistances[i1];
            double t = (a - distance) / (a - b);
            return i0 + t;
        }

        /**
         * @eegeo.internal
         */
        private static int FirstZoomLevelLessThanDistance(double distance) {
            for (int i = 0; i < ms_zoomToDistances.length; ++i) {
                if (ms_zoomToDistances[i] < distance) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Sets the target point of the camera.
         *
         * @param latitude  Latitude in degrees.
         * @param longitude Longitude in degrees.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder target(double latitude, double longitude) {
            this.m_target = new LatLng(latitude, longitude);
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
            this.m_target = new LatLng(latitude, longitude);
            this.m_targetElevation = altitude;
            this.m_targetElevationMode = ElevationMode.HeightAboveSeaLevel;
            return this;
        }

        /**
         * Sets the target point of the camera.
         *
         * @param latLon Target point.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder target(@NonNull LatLng latLon) {
            this.m_target = new LatLng(latLon.latitude, latLon.longitude);
            return this;
        }

        /**
         * Sets the target point of the camera.
         *
         * @param latLonAlt Target point.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder target(@NonNull LatLngAlt latLonAlt) {
            this.m_target = latLonAlt.toLatLng();
            this.m_targetElevation = latLonAlt.altitude;
            this.m_targetElevationMode = ElevationMode.HeightAboveSeaLevel;
            return this;
        }

        /**
         * Sets the targetElevation for the CameraPosition. The default targetElevation is 0.
         * Note that this is an experimental field - values other than 0 may have unexpected effects on camera control
         *
         * @param elevation The elevation, in meters.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder elevation(@NonNull double elevation) {
            this.m_targetElevation = elevation;
            return this;
        }

        /**
         * Sets the targetElevationMode for the CameraPosition. The default targetElevationMode is ElevationMode.HeightAboveGround.
         * Note that this is an experimental field - values other than ElevationMode.HeightAboveGround may have unexpected effects on camera control
         *
         * @param elevationMode The ElevationMode used to interpret the targetElevation.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder elevationMode(@NonNull ElevationMode elevationMode) {
            this.m_targetElevationMode = elevationMode;
            return this;
        }

        /**
         * Sets the indoor map properties for the CameraPosition target. If this method is not called,
         * the builder is initialised to create a CameraPosition with its interest point on an outdoor map.
         *
         * @param indoorMapId The identifier of the indoor map of the camera target interest point.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder indoor(String indoorMapId) {
            this.m_targetIndoorMapId = indoorMapId;
            this.m_targetIndoorMapDefaultFloor = true;
            return this;
        }

        /**
         * Sets the indoor map properties for the CameraPosition target. If this method is not called,
         * the builder is initialised to create a CameraPosition with its interest point on an outdoor map.
         *
         * @param indoorMapId The identifier of the indoor map of the camera target interest point.
         * @param indoorMapFloorId The identifier of the indoor map floor of the camera target interest point.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder indoor(String indoorMapId, int indoorMapFloorId) {
            this.m_targetIndoorMapId = indoorMapId;
            this.m_targetIndoorMapFloorId = indoorMapFloorId;
            this.m_targetIndoorMapDefaultFloor = false;
            return this;
        }

        /**
         * Sets the camera zoom.
         *
         * @param zoom The new zoom value.
         * @return Updated CameraPosition.Builder object.
         */
        public Builder zoom(double zoom) {
            this.m_zoom = zoom;
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
            return this;
        }

        /**
         * Sets the orientation of the camera in the earth tangent plane, in degrees clockwise from north.
         *
         * @param bearing The direction that the camera is facing, in degrees clockwise from north.
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
            return this;
        }

        /**
         * Sets the distance from the camera to its target interest point.
         *
         * @param distance The distance, in meters, from the camera location to the target interest point.
         * @return Updated CameraPosition.Builder object.
         * @eegeo.internal
         */
        public Builder distance(double distance) {
            return zoom(DistanceToZoom(distance));
        }

        /**
         * Builds a CameraPosition object.
         *
         * @return The final CameraPosition object.
         */
        public final CameraPosition build() {

            return new CameraPosition(
                    m_target,
                    m_targetElevation,
                    m_targetElevationMode,
                    m_targetIndoorMapId,
                    m_targetIndoorMapFloorId,
                    m_targetIndoorMapDefaultFloor,
                    m_zoom,
                    m_tilt,
                    m_bearing
                    );
        }
    }
}

