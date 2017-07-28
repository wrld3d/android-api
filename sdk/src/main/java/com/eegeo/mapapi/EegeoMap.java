package com.eegeo.mapapi;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.camera.CameraApiJniCalls;
import com.eegeo.mapapi.camera.CameraPosition;
import com.eegeo.mapapi.camera.CameraUpdate;
import com.eegeo.mapapi.camera.CameraUpdateFactory;
import com.eegeo.mapapi.camera.Projection;
import com.eegeo.mapapi.geometry.LatLngAlt;
import com.eegeo.mapapi.geometry.LatLngBounds;
import com.eegeo.mapapi.indoors.ExpandFloorsJniCalls;
import com.eegeo.mapapi.indoors.IndoorMap;
import com.eegeo.mapapi.indoors.IndoorsApiJniCalls;
import com.eegeo.mapapi.indoors.OnFloorChangedListener;
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener;
import com.eegeo.mapapi.indoors.OnIndoorExitedListener;
import com.eegeo.mapapi.map.EegeoMapOptions;
import com.eegeo.mapapi.map.OnInitialStreamingCompleteListener;
import com.eegeo.mapapi.markers.Marker;
import com.eegeo.mapapi.markers.MarkerApi;
import com.eegeo.mapapi.markers.MarkerOptions;
import com.eegeo.mapapi.markers.OnMarkerClickListener;
import com.eegeo.mapapi.util.Callbacks;
import com.eegeo.mapapi.util.Ready;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class representing a map. This class is the entry point for methods which change the camera, manage markers,
 * and handle indoor maps.
 *
 * An instance of this class can be obtained through the OnMapReadyCallback passed to the getMapAsync
 * method on the MapView object.
 *
 * Public methods on this class should be called from the Android UI thread.
 */
public final class EegeoMap {
    private final long m_eegeoMapApiPtr;
    private final INativeMessageRunner m_nativeRunner;
    private final IUiMessageRunner m_uiRunner;
    private List<OnCameraMoveListener> m_onCameraMoveListeners = new ArrayList<>();
    private List<OnMapClickListener> m_onMapClickedListeners = new ArrayList<>();
    private List<OnIndoorEnteredListener> m_onIndoorEnteredListeners = new ArrayList<>();
    private List<OnIndoorExitedListener> m_onIndoorExitedListeners = new ArrayList<>();
    private List<OnFloorChangedListener> m_onIndoorFloorChangedListeners = new ArrayList<>();
    private List<OnInitialStreamingCompleteListener> m_onInitialStreamingCompleteListeners = new ArrayList<>();
    private CameraPosition m_cameraPosition = new CameraPosition.Builder().build();
    private IndoorMap m_indoorMap = null;
    private int m_currentIndoorFloor = -1;
    private Projection m_projection;
    private MarkerApi m_markerApi;

    @WorkerThread
    EegeoMap(INativeMessageRunner nativeRunner,
             IUiMessageRunner uiRunner,
             EegeoNativeMapView.ICreateEegeoMapApi createNativeEegeoMapApi,
             EegeoMapOptions eegeoMapOptions
    ) {
        this.m_uiRunner = uiRunner;
        this.m_nativeRunner = nativeRunner;
        this.m_eegeoMapApiPtr = createNativeEegeoMapApi.create(this, eegeoMapOptions);
        this.m_projection = new Projection(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_markerApi = new MarkerApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
    }

    @WorkerThread
    void initialise(@NonNull EegeoMapOptions eegeoMapOptions) {
        initLocation(eegeoMapOptions.getCamera());
    }

    /**
     * Animates the camera change from its current position to a new position.
     *
     * @param update     Specifies the new position, either by specifying the new camera position, or by describing the desired display area.
     * @param durationMs Length of time for the transition, in ms.
     */
    @UiThread
    public void animateCamera(@NonNull CameraUpdate update, int durationMs) {
        switch (update.getUpdateType()) {
            case CameraPosition:
                performAnimateCamera((CameraUpdateFactory.IdentityCameraPositionUpdate) update, durationMs);
                break;
            case LatLngBounds:
                performAnimateCamera((CameraUpdateFactory.LatLongBoundsCameraPositionUpdate) update, durationMs);
                break;
        }
    }

    @UiThread
    private void performAnimateCamera(@NonNull final CameraUpdateFactory.IdentityCameraPositionUpdate cameraPositionUpdate, final int durationMs) {
        final CameraPosition cameraPosition = cameraPositionUpdate.getCameraPosition();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                double latitude = (cameraPosition.target != null) ? cameraPosition.target.latitude : 0.0;
                double longitude = (cameraPosition.target != null) ? cameraPosition.target.longitude : 0.0;
                double altitude = (cameraPosition.target != null) ? cameraPosition.target.altitude : 0.0;
                CameraApiJniCalls.setView(m_eegeoMapApiPtr, true,
                        latitude, longitude, altitude, cameraPosition.modifyTarget,
                        cameraPosition.distance, cameraPosition.modifyDistance,
                        cameraPosition.bearing, cameraPosition.modifyBearing,
                        cameraPosition.tilt, cameraPosition.modifyTilt,
                        durationMs / 1000.0, true,
                        true, true);
            }
        });
    }

    @UiThread
    private void performAnimateCamera(@NonNull final CameraUpdateFactory.LatLongBoundsCameraPositionUpdate latLongBoundsUpdate, final int durationMs) {
        //TODO: support for durationMs
        final LatLngBounds latLngBounds = latLongBoundsUpdate.getLatLngBounds();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                CameraApiJniCalls.setViewToBounds(m_eegeoMapApiPtr, true, latLngBounds.northeast.latitude, latLngBounds.northeast.longitude,
                        latLngBounds.southwest.latitude, latLngBounds.southwest.longitude,
                        true);
            }
        });
    }

    /**
     * Moves the camera change from its current position to a new position.
     *
     * @param update Specifies the new position, either by specifying the new camera position, or by describing the desired display area.
     */
    @UiThread
    public void moveCamera(@NonNull CameraUpdate update) {
        switch (update.getUpdateType()) {
            case CameraPosition:
                performMoveCamera((CameraUpdateFactory.IdentityCameraPositionUpdate) update);
                break;
            case LatLngBounds:
                performMoveCamera((CameraUpdateFactory.LatLongBoundsCameraPositionUpdate) update);
                break;
        }
    }

    @UiThread
    private void performMoveCamera(@NonNull final CameraUpdateFactory.IdentityCameraPositionUpdate cameraPositionUpdate) {
        final CameraPosition cameraPosition = cameraPositionUpdate.getCameraPosition();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                double latitude = (cameraPosition.target != null) ? cameraPosition.target.latitude : 0.0;
                double longitude = (cameraPosition.target != null) ? cameraPosition.target.longitude : 0.0;
                double altitude = (cameraPosition.target != null) ? cameraPosition.target.altitude : 0.0;
                CameraApiJniCalls.setView(m_eegeoMapApiPtr, true,
                        latitude, longitude, altitude, cameraPosition.modifyTarget,
                        cameraPosition.distance, cameraPosition.modifyDistance,
                        cameraPosition.bearing, cameraPosition.modifyBearing,
                        cameraPosition.tilt, cameraPosition.modifyTilt,
                        0.0, true,
                        true, true);
            }
        });
    }

    @UiThread
    private void performMoveCamera(@NonNull final CameraUpdateFactory.LatLongBoundsCameraPositionUpdate latLongBoundsUpdate) {
        final LatLngBounds latLngBounds = latLongBoundsUpdate.getLatLngBounds();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                CameraApiJniCalls.setViewToBounds(m_eegeoMapApiPtr, true, latLngBounds.northeast.latitude, latLngBounds.northeast.longitude,
                        latLngBounds.southwest.latitude, latLngBounds.southwest.longitude,
                        true);
            }
        });
    }

    @WorkerThread
    private void initLocation(@NonNull CameraPosition cameraPosition) {
        CameraApiJniCalls.initView(m_eegeoMapApiPtr,
                cameraPosition.target.latitude,
                cameraPosition.target.longitude,
                cameraPosition.target.altitude,
                cameraPosition.distance,
                cameraPosition.bearing,
                cameraPosition.tilt, cameraPosition.modifyTilt);
    }

    /**
     * Returns the current projection.
     *
     * @return The current projection.
     */
    @UiThread
    public Projection getProjection() {
        return m_projection;
    }

    /**
     * Stops any running camera animation.
     */
    @UiThread
    public void stopAnimation() {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                CameraApiJniCalls.tryStopTransition(m_eegeoMapApiPtr);
            }
        });
    }

    /**
     * Registers a listener for camera move events.
     *
     * @param listener The listener to be notified.
     */
    @UiThread
    public void addOnCameraMoveListener(@NonNull OnCameraMoveListener listener) {
        m_onCameraMoveListeners.add(listener);
    }

    /**
     * Unregisters a listener for camera move events.
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeOnCameraMoveListener(@NonNull OnCameraMoveListener listener) {
        m_onCameraMoveListeners.remove(listener);
    }

    /**
     * Registers a listener for user map point selection events.
     *
     * @param listener The listener to be notified.
     */
    @UiThread
    public void addOnMapClickListener(@NonNull OnMapClickListener listener) {
        m_onMapClickedListeners.add(listener);
    }

    /**
     * Unregisters a listener for user map point selection events.
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeOnMapClickListener(@NonNull OnMapClickListener listener) {
        m_onMapClickedListeners.remove(listener);
    }

    /**
     * Gets the current camera position.
     *
     * @return The current camera position.
     */
    @UiThread
    public final CameraPosition getCameraPosition() {
        return m_cameraPosition;
    }

    /**
     * Sets the camera to the specified CameraPosition.  No transition or animation is applied.
     *
     * @param cameraPosition The new camera position.
     */
    @UiThread
    public void setCameraPosition(@NonNull CameraPosition cameraPosition) {
        moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Called from the SDK when the screen has been tapped.
     *
     * @param point Screen coordinates of the tapped point.
     */
    @UiThread
    public void onTapped(final Point point) {
        m_projection.fromScreenLocation(point)
                .then(new Ready<LatLngAlt>() {
                    @UiThread
                    @Override
                    public void ready(LatLngAlt latLngAlt) {
                        for (OnMapClickListener listener : m_onMapClickedListeners) {
                            listener.onMapClick(latLngAlt);
                        }
                    }
                });
    }

    @WorkerThread
    private void jniOnCameraMove() {
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                for (OnCameraMoveListener listener : m_onCameraMoveListeners) {
                    listener.onCameraMove();
                }
            }
        });
    }

    @WorkerThread
    private void jniUpdateCameraState(final double lat, final double lon, final double interestAltitude, final double bearing, final double tilt, final double distance) {
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                double zoom = CameraPosition.Builder.DistanceToZoom(distance);

                m_cameraPosition = new CameraPosition(new LatLngAlt(lat, lon, interestAltitude), zoom, tilt, bearing);
            }
        });
    }

    /**
     * Show the expanded indoor map view.
     */
    @UiThread
    public void expandIndoor() {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            public void run() {
                ExpandFloorsJniCalls.expandIndoor(m_eegeoMapApiPtr);
            }
        });
    }

    /**
     * Show the collapsed indoor map view.
     */
    @UiThread
    public void collapseIndoor() {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                ExpandFloorsJniCalls.collapseIndoor(m_eegeoMapApiPtr);
            }
        });
    }

    /**
     * Gets the active indoor map.
     *
     * @return The active indoor map, or null if no indoor map is active.
     */
    @UiThread
    public IndoorMap getActiveIndoorMap() {
        return m_indoorMap;
    }

    /**
     * Gets the active floor of the current indoor map.
     *
     * @return The index of the active floor of the current indoor map, or -1 if no indoor map is active.
     */
    @UiThread
    public int getCurrentFloorIndex() {
        return m_currentIndoorFloor;
    }

    /**
     * Registers a listener for indoor map entrance events.
     *
     * @param listener The listener to be notified when the user taps an indoor entrance marker.
     */
    @UiThread
    public void addOnIndoorEnteredListener(@NonNull OnIndoorEnteredListener listener) {
        m_onIndoorEnteredListeners.add(listener);
    }

    /**
     * Unregisters a listener for indoor map entrance events.
     *
     * @param listener The listener to be removed
     */
    @UiThread
    public void removeOnIndoorEnteredListener(@NonNull OnIndoorEnteredListener listener) {
        m_onIndoorEnteredListeners.remove(listener);
    }

    @WorkerThread
    private void jniOnIndoorEntered() {
        final IndoorMap indoorMap = IndoorsApiJniCalls.getIndoorMapData(m_eegeoMapApiPtr);
        final int currentIndoorFloor = IndoorsApiJniCalls.getCurrentFloorIndex(m_eegeoMapApiPtr);
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                m_indoorMap = indoorMap;
                m_currentIndoorFloor = currentIndoorFloor;
                for (OnIndoorEnteredListener listener : m_onIndoorEnteredListeners) {
                    listener.onIndoorEntered();
                }
            }
        });
    }

    /**
     * Registers a listener for indoor map exit events.
     *
     * @param listener The listener to be notified when the user exits an indoor map.
     */
    @UiThread
    public void addOnIndoorExitedListener(@NonNull OnIndoorExitedListener listener) {
        m_onIndoorExitedListeners.add(listener);
    }

    /**
     * Unregisters a listener for indoor map exit events.
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeOnIndoorExitedListener(@NonNull OnIndoorExitedListener listener) {
        m_onIndoorExitedListeners.remove(listener);
    }

    /**
     * Exits the current indoor map.
     */
    @UiThread
    public void onExitIndoorClicked() {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            public void run() {
                IndoorsApiJniCalls.exitIndoorMap(m_eegeoMapApiPtr);
            }
        });
    }

    @WorkerThread
    private void jniOnIndoorExited() {
        m_uiRunner.runOnUiThread(new Runnable() {
            public void run() {
                m_indoorMap = null;
                m_currentIndoorFloor = -1;
                for (OnIndoorExitedListener listener : m_onIndoorExitedListeners) {
                    listener.onIndoorExited();
                }
            }
        });
    }

    /**
     * Sets the current floor shown in an indoor map.
     *
     * @param selectedFloor The index of the floor to be shown, relative to the
     *                      array returned by IndoorMap.getFloorIds()
     */
    @UiThread
    public void setIndoorFloor(final int selectedFloor) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                IndoorsApiJniCalls.floorSelected(m_eegeoMapApiPtr, selectedFloor);
            }
        });
    }

    /**
     * Moves up one floor in the current indoor map.
     */
    @UiThread
    public void moveIndoorUp() {
        moveIndoorUp(1);
    }

    /**
     * Moves up a number of floors in the current indoor map.
     *
     * @param numberOfFloors The number of floors to move up.
     */
    @UiThread
    public void moveIndoorUp(final int numberOfFloors) {
        moveIndoorFloors(numberOfFloors);
    }

    /**
     * Moves down one floor in the current indoor map.
     */
    @UiThread
    public void moveIndoorDown() {
        moveIndoorDown(1);
    }

    /**
     * Moves down a number of floors in the current indoor map.
     *
     * @param numberOfFloors The number of floors to move down
     */
    @UiThread
    public void moveIndoorDown(final int numberOfFloors) {
        moveIndoorFloors(-numberOfFloors);
    }

    private void moveIndoorFloors(final int delta) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            public void run() {
                m_currentIndoorFloor = IndoorsApiJniCalls.getCurrentFloorIndex(m_eegeoMapApiPtr);
                IndoorsApiJniCalls.floorSelected(m_eegeoMapApiPtr, m_currentIndoorFloor + delta);
            }
        });
    }

    /**
     * Sets the interpolation value used in the expanded indoor view.
     *
     * @param dragParameter The float value, in the range 0 .. number of floors.
     */
    @UiThread
    public void setIndoorFloorInterpolation(final float dragParameter) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            public void run() {
                IndoorsApiJniCalls.floorSelectionDragged(m_eegeoMapApiPtr, dragParameter);
            }
        });
    }

    /**
     * Registers a listener for indoor map floor change events.
     *
     * @param listener The listener to be notified.
     */
    @UiThread
    public void addOnFloorChangedListener(@NonNull OnFloorChangedListener listener) {
        m_onIndoorFloorChangedListeners.add(listener);
    }

    /**
     * Unregisters a listener for indoor map floor change events.
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeOnFloorChangedListener(@NonNull OnFloorChangedListener listener) {
        m_onIndoorFloorChangedListeners.remove(listener);
    }

    @WorkerThread
    private void jniOnFloorChanged(final int selectedFloor) {
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                m_currentIndoorFloor = selectedFloor;
                for (OnFloorChangedListener listener : m_onIndoorFloorChangedListeners) {
                    listener.onFloorChanged(m_currentIndoorFloor);
                }
            }
        });
    }

    /**
     * Create a marker and add it to the map.
     *
     * @param markerOptions Creation parameters for the marker
     * @return The Marker that was added
     */
    @UiThread
    public Marker addMarker(@NonNull final MarkerOptions markerOptions) {

        return new Marker(m_markerApi, markerOptions);
    }

    /**
     * Remove a marker from the map and destroy it.
     *
     * @param marker The Marker to remove.
     */
    @UiThread
    public void removeMarker(@NonNull final Marker marker) {

        marker.destroy();
    }

    /**
     * Register a listener to an event raised when a marker is tapped by the user.
     *
     * @param listener the listener to add
     */
    @UiThread
    public void addMarkerClickListener(@NonNull OnMarkerClickListener listener) {
        m_markerApi.addMarkerClickListener(listener);
    }

    /**
     * Unregister a listener to an event raised when a marker is tapped by the user.
     *
     * @param listener the listener to remove
     */
    @UiThread
    public void removeMarkerClickListener(@NonNull OnMarkerClickListener listener) {
        m_markerApi.removeMarkerClickListener(listener);
    }

    @WorkerThread
    private void jniOnMarkerClicked(final int markerId) {
        m_markerApi.notifyMarkerClicked(markerId);
    }

    /**
     * Registers a listener to an event raised when the initial map scene has completed streaming all resources
     *
     * @param listener The listener to be notified.
     */
    @UiThread
    public void addInitialStreamingCompleteListener(@NonNull OnInitialStreamingCompleteListener listener) {
        m_onInitialStreamingCompleteListeners.add(listener);
    }

    /**
     * Unregisters a listener of the initial streaming complete event
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeInitialStreamingCompleteListener(@NonNull OnInitialStreamingCompleteListener listener) {
        m_onInitialStreamingCompleteListeners.remove(listener);
    }

    @WorkerThread
    private void jniNotifyInitialStreamingComplete() {
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                for (OnInitialStreamingCompleteListener listener : m_onInitialStreamingCompleteListeners) {
                    listener.onInitialStreamingComplete();
                }
            }
        });
    }

    /**
     * Interface for objects which receive notifications of changes to the camera.
     */
    public interface OnCameraMoveListener {
        @UiThread
        void onCameraMove();
    }

    /**
     * Interface for objects which receive notifications when the user taps on the map.
     */
    public interface OnMapClickListener {
        @UiThread
        void onMapClick(LatLngAlt point);
    }

    private class OnCameraMoveListenerImpl implements Callbacks.ICallback0 {
        private OnCameraMoveListener m_listener;

        @UiThread
        public OnCameraMoveListenerImpl(OnCameraMoveListener listener) {
            this.m_listener = listener;
        }

        @UiThread
        public void onCallback() {
            m_listener.onCameraMove();
        }
    }

}

