package com.eegeo.mapapi;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.eegeo.mapapi.bluesphere.BlueSphere;
import com.eegeo.mapapi.bluesphere.BlueSphereApi;
import com.eegeo.mapapi.buildings.BuildingHighlight;
import com.eegeo.mapapi.buildings.BuildingsApi;
import com.eegeo.mapapi.buildings.BuildingHighlightOptions;
import com.eegeo.mapapi.camera.CameraApiJniCalls;
import com.eegeo.mapapi.camera.CameraPosition;
import com.eegeo.mapapi.camera.CameraUpdate;
import com.eegeo.mapapi.camera.CameraUpdateFactory;
import com.eegeo.mapapi.camera.Projection;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.LatLngAlt;
import com.eegeo.mapapi.geometry.LatLngBounds;
import com.eegeo.mapapi.geometry.MapFeatureType;
import com.eegeo.mapapi.picking.PickResult;
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
import com.eegeo.mapapi.positioner.Positioner;
import com.eegeo.mapapi.positioner.PositionerApi;
import com.eegeo.mapapi.positioner.PositionerOptions;
import com.eegeo.mapapi.positioner.OnPositionerChangedListener;
import com.eegeo.mapapi.picking.PickingApi;
import com.eegeo.mapapi.polygons.Polygon;
import com.eegeo.mapapi.polygons.PolygonApi;
import com.eegeo.mapapi.polygons.PolygonOptions;
import com.eegeo.mapapi.polylines.Polyline;
import com.eegeo.mapapi.polylines.PolylineApi;
import com.eegeo.mapapi.polylines.PolylineOptions;
import com.eegeo.mapapi.rendering.RenderingApi;
import com.eegeo.mapapi.rendering.RenderingState;
import com.eegeo.mapapi.services.poi.PoiApi;
import com.eegeo.mapapi.services.poi.PoiService;
import com.eegeo.mapapi.services.poi.PoiSearchResult;
import com.eegeo.mapapi.services.routing.Route;
import com.eegeo.mapapi.services.routing.RoutingApi;
import com.eegeo.mapapi.services.routing.RoutingService;
import com.eegeo.mapapi.util.Callbacks;
import com.eegeo.mapapi.util.Promise;
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
    private PositionerApi m_positionerApi;
    private PolygonApi m_polygonApi;
    private PolylineApi m_polylineApi;
    private BlueSphereApi m_blueSphereApi;
    private BuildingsApi m_buildingsApi;
    private PickingApi m_pickingApi;
    private RenderingApi m_renderingApi;
    private RenderingState m_renderingState;
    private PoiApi m_poiApi;
    private RoutingApi m_routingApi;
    private BlueSphere m_blueSphere = null;



    private static final AllowApiAccess m_allowApiAccess = new AllowApiAccess();

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
        this.m_positionerApi = new PositionerApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_polygonApi = new PolygonApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_polylineApi = new PolylineApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_blueSphereApi = new BlueSphereApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_buildingsApi = new BuildingsApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_pickingApi = new PickingApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_renderingApi = new RenderingApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        boolean mapCollapsed = false;
        this.m_renderingState = new RenderingState(m_renderingApi, m_allowApiAccess, mapCollapsed);
        this.m_poiApi = new PoiApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_routingApi = new RoutingApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
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
    @Deprecated
    public void onExitIndoorClicked() {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            public void run() {
                IndoorsApiJniCalls.exitIndoorMap(m_eegeoMapApiPtr);
            }
        });
    }

    /**
     * Exits the current indoor map.
     */
    @UiThread
    public void exitIndoorMap() {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            public void run() {
                IndoorsApiJniCalls.exitIndoorMap(m_eegeoMapApiPtr);
            }
        });
    }

    /**
     * Enters the specified indoor map.
     * @param indoorMapId The id of the indoor map to enter
     */
    @UiThread
    public void enterIndoorMap(final String indoorMapId) {
        m_nativeRunner.runOnNativeThread(new Runnable()
        {
            @Override
            public void run()
            {
                IndoorsApiJniCalls.enterIndoorMap(m_eegeoMapApiPtr, indoorMapId);
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

    @WorkerThread
    private void jniOnIndoorEnterFailed() {
        m_uiRunner.runOnUiThread(new Runnable() {
            public void run() {
                m_indoorMap = null;
                m_currentIndoorFloor = -1;
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
     * Create a positioner and add it to the map.
     *
     * @param positionerOptions Creation parameters for the positioner
     * @return The Positioner that was added
     */
    @UiThread
    public Positioner addPositioner(@NonNull final PositionerOptions positionerOptions) {

        return new Positioner(m_positionerApi, positionerOptions);
    }

    /**
     * Remove a positioner from the map and destroy it.
     *
     * @param positioner The Positioner to remove.
     */
    @UiThread
    public void removePositioner(@NonNull final Positioner positioner) {

        positioner.destroy();
    }

    /**
     * Create a polygon and add it to the map.
     *
     * @param polygonOptions Creation parameters for the marker
     * @return The Polygon that was added
     */
    @UiThread
    public Polygon addPolygon(@NonNull final PolygonOptions polygonOptions) {

        return new Polygon(m_polygonApi, polygonOptions);
    }


    /**
     * Remove a polygon from the map and destroy it.
     *
     * @param polygon The Polygon to remove.
     */
    @UiThread
    public void removePolygon(@NonNull final Polygon polygon) {

        polygon.destroy();
    }

    /**
     * Create a polyline and add it to the map.
     *
     * @param polylineOptions Creation parameters for the polyline
     * @return The Polyline that was added
     */
    @UiThread
    public Polyline addPolyline(@NonNull final PolylineOptions polylineOptions) {

        return new Polyline(m_polylineApi, polylineOptions);
    }


    /**
     * Remove a polyline from the map and destroy it.
     *
     * @param polyline The Polyline to remove.
     */
    @UiThread
    public void removePolyline(@NonNull final Polyline polyline) {

        polyline.destroy();
    }

    /**
     * Create a building highlight and add it to the map.
     *
     * @param buildingHighlightOptions Creation parameters for the building highlight
     * @return The BuildingHighlight that was added
     */
    @UiThread
    public BuildingHighlight addBuildingHighlight(@NonNull final BuildingHighlightOptions buildingHighlightOptions) {

        return new BuildingHighlight(m_buildingsApi, buildingHighlightOptions);
    }


    /**
     * Remove a BuildingHighlight from the map and destroy it.
     *
     * @param buildingHighlight The BuildingHighlight to remove.
     */
    @UiThread
    public void removeBuildingHighlight(@NonNull final BuildingHighlight buildingHighlight) {

        buildingHighlight.destroy();
    }

    /**
     * Attempts to find a map feature at the given screen point. A ray is constructed from the
     * camera location and passing through the screen point. The first intersection of the ray with
     * any of the currently streamed map features is found, if any.
     * See PickResult for details of information returned.
     *
     * @param point A screen space point, in units of pixels with the origin at the top left
     *              corner of the screen.
     * @return A promise to provide information about the map feature intersected with, if any.
     * @eegeo.codeintro The value of the promise can be accessed through an object implementing the Ready interface, for example:
     * @eegeo.code <pre>
     * map.pickFeatureAtScreenPoint(point)
     *           .then(new Ready&lt;PickResult&gt;() {
     *               public void ready(PickResult pickResult) {
     *                   // use value of pickResult here
     *               }
     *           }
     * );
     * </pre>
     */
    @UiThread
    public Promise<PickResult> pickFeatureAtScreenPoint(@NonNull final Point point) {
        return m_pickingApi.pickFeatureAtScreenPoint(point);
    }

    /**
     * Attempts to find a map feature at the given LatLng location.
     * See PickResult for details of information returned.
     * @param latLng A LatLng coordinate.
     * @return A promise to provide information about the map feature intersected with, if any.
     * @eegeo.codeintro The value of the promise can be accessed through an object implementing the Ready interface, for example:
     * @eegeo.code <pre>
     * map.pickFeatureAtLatLng(point)
     *           .then(new Ready&lt;PickResult&gt;() {
     *               public void ready(PickResult pickResult) {
     *                   // use value of pickResult here
     *               }
     *           }
     * );
     * </pre>
     */
    @UiThread
    public Promise<PickResult> pickFeatureAtLatLng(@NonNull final LatLng latLng) {
        return m_pickingApi.pickFeatureAtLatLng(latLng);
    }

    /**
     * Gets the BlueSphere
     *
     * @return  The BlueSphere.
     */
    @UiThread
    public BlueSphere getBlueSphere() {
        if(m_blueSphere == null)
        {
            m_blueSphere = new BlueSphere(m_blueSphereApi);
        }

        return m_blueSphere;
    }

    /**
     * Sets whether the map view should display with vertical scaling applied so that terrain and
     * other map features appear flattened.
     */
    @UiThread
    public void setMapCollapsed(final boolean isCollapsed) {
        m_renderingState.setMapCollapsed(isCollapsed);
    }

    @UiThread
    public boolean isMapCollapsed() {
        return m_renderingState.isMapCollapsed();
    }

    /**
     * Creates and returns a PoiService for this map.
     */
    @UiThread
    public PoiService createPoiService() {
        PoiService poiService = new PoiService(m_poiApi);
        return poiService;
    }

    /**
     * Creates and returns a RoutingService for this map.
     */
    @UiThread
    public RoutingService createRoutingService() {
        RoutingService routingService = new RoutingService(m_routingApi);
        return routingService;
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

    /**
     * Register a listener to an event raised when a Positioner object has changed position.
     *
     * @param listener the listener to add
     */
    @UiThread
    public void addPositionerChangedListener(@NonNull OnPositionerChangedListener listener) {
        m_positionerApi.addPositionerChangedListener(listener);
    }

    /**
     * Unregister a listener to an event raised when a Positioner object has changed position.
     *
     * @param listener the listener to remove
     */
    @UiThread
    public void removePositionerChangedListener(@NonNull OnPositionerChangedListener listener) {
        m_positionerApi.removePositionerChangedListener(listener);
    }

    @WorkerThread
    private void jniOnMarkerClicked(final int markerId) {
        m_markerApi.notifyMarkerClicked(markerId);
    }

    @WorkerThread
    private void jniOnBuildingInformationReceived(final int buildingHighlightId) {
        m_buildingsApi.notifyBuildingInformationReceived(buildingHighlightId);
    }

    @WorkerThread
    private void jniOnPositionerProjectionChanged() {
        m_positionerApi.notifyProjectionChanged();
    }

    @WorkerThread
    private void jniOnPoiSearchCompleted(final int poiSearchId, final boolean succeeded, final List<PoiSearchResult> searchResults) {
        m_poiApi.notifySearchComplete(poiSearchId, succeeded, searchResults);
    }

    @WorkerThread
    private void jniOnRoutingQueryCompleted(final int routingQueryId, final boolean succeeded, final List<Route> routingResults) {
        m_routingApi.notifyQueryComplete(routingQueryId, succeeded, routingResults);
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

    public static final class AllowApiAccess {
        @WorkerThread
        private AllowApiAccess() {
        }
    }

}

