package com.eegeo.mapapi.camera;

import android.support.annotation.NonNull;

import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.LatLngBounds;

/**
 * Factory class for creating CameraUpdate objects.  These can then be passed to
 * camera operations on the EegeoMap object.
 */
public final class CameraUpdateFactory {
    private CameraUpdateFactory() {
    }

    /**
     * Creates a CameraUpdate object from an existing CameraPosition.
     *
     * @param cameraPosition The CameraPosition to encapsulate.
     * @return A CameraUpdate object which specifies the camera position.
     */
    public static CameraUpdate newCameraPosition(@NonNull CameraPosition cameraPosition) {
        return new IdentityCameraPositionUpdate(cameraPosition);
    }

    /**
     * Creates a CameraUpdate object from a LatLng location.
     *
     * @param latLng The LatLng location for the camera.
     * @return A CameraUpdate object which specifies the camera location.
     */
    public static CameraUpdate newLatLng(@NonNull LatLng latLng) {
        return new IdentityCameraPositionUpdate(new CameraPosition.Builder().target(latLng).build());
    }

    /**
     * Creates a CameraUpdate object from a LatLngBounds area.
     *
     * @param bounds  The bounds to be displayed.
     * @param padding Unused. For future expansion.
     * @return A CameraUpdate object which displays the specified area.
     */
    public static CameraUpdate newLatLngBounds(@NonNull LatLngBounds bounds, int padding) {
        return new LatLongBoundsCameraPositionUpdate(bounds);
    }

    /**
     * Internal class for building CameraUpdate objects from camera position.
     *
     * @eegeo.internal
     */
    public static final class IdentityCameraPositionUpdate implements CameraUpdate {
        private final CameraPosition m_cameraPosition;

        public IdentityCameraPositionUpdate(@NonNull CameraPosition position) {
            this.m_cameraPosition = position;
        }

        public final CameraPosition getCameraPosition() {
            return m_cameraPosition;
        }

        @Override
        public CameraUpdateType getUpdateType() {
            return CameraUpdateType.CameraPosition;
        }
    }

    /**
     * Internal class for building CameraUpdate objects from LatLngBounds
     *
     * @eegeo.internal
     */
    public static final class LatLongBoundsCameraPositionUpdate implements CameraUpdate {
        private final LatLngBounds m_latLngBounds;

        public LatLongBoundsCameraPositionUpdate(@NonNull LatLngBounds latLngBounds) {
            this.m_latLngBounds = latLngBounds;
        }

        public final LatLngBounds getLatLngBounds() {
            return m_latLngBounds;
        }

        @Override
        public CameraUpdateType getUpdateType() {
            return CameraUpdateType.LatLngBounds;
        }
    }
}
