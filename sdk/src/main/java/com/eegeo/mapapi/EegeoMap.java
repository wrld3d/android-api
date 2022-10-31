package com.eegeo.mapapi;

import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.bluesphere.BlueSphere;
import com.eegeo.mapapi.bluesphere.BlueSphereApi;
import com.eegeo.mapapi.buildings.BuildingHighlight;
import com.eegeo.mapapi.buildings.BuildingHighlightOptions;
import com.eegeo.mapapi.buildings.BuildingsApi;
import com.eegeo.mapapi.camera.CameraAnimationOptions;
import com.eegeo.mapapi.camera.CameraApi;
import com.eegeo.mapapi.camera.CameraPosition;
import com.eegeo.mapapi.camera.CameraUpdate;
import com.eegeo.mapapi.camera.CameraUpdateFactory;
import com.eegeo.mapapi.camera.Projection;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.LatLngAlt;
import com.eegeo.mapapi.indoorentities.IndoorEntityApi;
import com.eegeo.mapapi.indoorentities.IndoorMapEntityInformation;
import com.eegeo.mapapi.indoorentities.IndoorMapEntityInformationApi;
import com.eegeo.mapapi.indoorentities.IndoorEntityPickedMessage;
import com.eegeo.mapapi.indoorentities.OnIndoorEntityPickedListener;
import com.eegeo.mapapi.indoorentities.OnIndoorMapEntityInformationChangedListener;
import com.eegeo.mapapi.indooroutlines.IndoorMapFloorOutlineInformation;
import com.eegeo.mapapi.indooroutlines.IndoorMapFloorOutlineInformationApi;
import com.eegeo.mapapi.indooroutlines.OnIndoorMapFloorOutlineInformationLoadedListener;
import com.eegeo.mapapi.indoors.ExpandFloorsJniCalls;
import com.eegeo.mapapi.indoors.IndoorMap;
import com.eegeo.mapapi.indoors.IndoorsApiJniCalls;
import com.eegeo.mapapi.indoors.OnFloorChangedListener;
import com.eegeo.mapapi.indoors.OnIndoorEnterFailedListener;
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener;
import com.eegeo.mapapi.indoors.OnIndoorExitedListener;
import com.eegeo.mapapi.indoors.OnIndoorMapLoadedListener;
import com.eegeo.mapapi.indoors.OnIndoorMapUnloadedListener;
import com.eegeo.mapapi.labels.LabelApi;
import com.eegeo.mapapi.map.EegeoMapOptions;
import com.eegeo.mapapi.map.OnInitialStreamingCompleteListener;
import com.eegeo.mapapi.markers.Marker;
import com.eegeo.mapapi.markers.MarkerApi;
import com.eegeo.mapapi.markers.MarkerOptions;
import com.eegeo.mapapi.markers.OnMarkerClickListener;
import com.eegeo.mapapi.picking.PickResult;
import com.eegeo.mapapi.picking.PickingApi;
import com.eegeo.mapapi.paths.PointOnPath;
import com.eegeo.mapapi.paths.PathApi;
import com.eegeo.mapapi.paths.PointOnRoute;
import com.eegeo.mapapi.paths.PointOnRouteOptions;
import com.eegeo.mapapi.polygons.Polygon;
import com.eegeo.mapapi.polygons.PolygonApi;
import com.eegeo.mapapi.polygons.PolygonOptions;
import com.eegeo.mapapi.polylines.Polyline;
import com.eegeo.mapapi.polylines.PolylineApi;
import com.eegeo.mapapi.polylines.PolylineOptions;
import com.eegeo.mapapi.heatmaps.Heatmap;
import com.eegeo.mapapi.heatmaps.HeatmapApi;
import com.eegeo.mapapi.heatmaps.HeatmapOptions;
import com.eegeo.mapapi.positioner.OnPositionerChangedListener;
import com.eegeo.mapapi.positioner.Positioner;
import com.eegeo.mapapi.positioner.PositionerApi;
import com.eegeo.mapapi.positioner.PositionerOptions;
import com.eegeo.mapapi.precaching.PrecacheApi;
import com.eegeo.mapapi.precaching.OnPrecacheOperationCompletedListener;
import com.eegeo.mapapi.precaching.PrecacheOperation;
import com.eegeo.mapapi.precaching.PrecacheOperationResult;
import com.eegeo.mapapi.props.Prop;
import com.eegeo.mapapi.props.PropOptions;
import com.eegeo.mapapi.props.PropsApi;
import com.eegeo.mapapi.rendering.RenderingApi;
import com.eegeo.mapapi.rendering.RenderingState;
import com.eegeo.mapapi.services.mapscene.Mapscene;
import com.eegeo.mapapi.services.mapscene.MapsceneApi;
import com.eegeo.mapapi.services.mapscene.MapsceneService;
import com.eegeo.mapapi.services.poi.PoiApi;
import com.eegeo.mapapi.services.poi.PoiSearchResult;
import com.eegeo.mapapi.services.poi.PoiService;
import com.eegeo.mapapi.services.routing.Route;
import com.eegeo.mapapi.services.tag.TagApi;
import com.eegeo.mapapi.services.tag.TagService;
import com.eegeo.mapapi.services.routing.RoutingQueryResponse;
import com.eegeo.mapapi.services.routing.RoutingApi;
import com.eegeo.mapapi.services.routing.RoutingService;
import com.eegeo.mapapi.util.Callbacks;
import com.eegeo.mapapi.util.Promise;
import com.eegeo.mapapi.util.Ready;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<OnIndoorEnterFailedListener> m_onIndoorEnterFailedListeners = new ArrayList<>();
    private List<OnIndoorMapLoadedListener> m_onIndoorMapLoadedListeners = new ArrayList<>();
    private List<OnIndoorMapUnloadedListener> m_onIndoorMapUnloadedListeners = new ArrayList<>();
    private List<OnFloorChangedListener> m_onIndoorFloorChangedListeners = new ArrayList<>();
    private List<OnInitialStreamingCompleteListener> m_onInitialStreamingCompleteListeners = new ArrayList<>();
    private CameraPosition m_cameraPosition = null;
    private IndoorMap m_indoorMap = null;
    private int m_currentIndoorFloor = -1;
    private Map<String, LatLngAlt> m_indoorMapEntryMarkerLocations = new HashMap<>();
    private boolean m_transitioningToIndoorMap = false;
    private Projection m_projection;
    private CameraApi m_cameraApi;
    private MarkerApi m_markerApi;
    private PositionerApi m_positionerApi;
    private PolygonApi m_polygonApi;
    private PolylineApi m_polylineApi;
    private HeatmapApi m_heatmapApi;
    private PropsApi m_propsApi;
    private BlueSphereApi m_blueSphereApi;
    private BuildingsApi m_buildingsApi;
    private PickingApi m_pickingApi;
    private RenderingApi m_renderingApi;
    private RenderingState m_renderingState;
    private PoiApi m_poiApi;
    private TagApi m_tagApi;
    private MapsceneApi m_mapsceneApi;
    private RoutingApi m_routingApi;
    private PathApi m_pathApi;
    private BlueSphere m_blueSphere = null;
    private PrecacheApi m_precacheApi;
    private IndoorEntityApi m_indoorEntityApi;
    private IndoorMapEntityInformationApi m_indoorMapEntityInformationApi;
    private IndoorMapFloorOutlineInformationApi m_indoorMapFloorOutlineInformationApi;
    private LabelApi m_labelApi;


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
        this.m_cameraApi = new CameraApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_markerApi = new MarkerApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_positionerApi = new PositionerApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_polygonApi = new PolygonApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_polylineApi = new PolylineApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_heatmapApi = new HeatmapApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_propsApi = new PropsApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_blueSphereApi = new BlueSphereApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_buildingsApi = new BuildingsApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_pickingApi = new PickingApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_renderingApi = new RenderingApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        boolean mapCollapsed = false;
        this.m_renderingState = new RenderingState(m_renderingApi, m_allowApiAccess, mapCollapsed);
        this.m_poiApi = new PoiApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_tagApi = new TagApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_mapsceneApi = new MapsceneApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_routingApi = new RoutingApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_pathApi = new PathApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_precacheApi = new PrecacheApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_indoorEntityApi = new IndoorEntityApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_indoorMapEntityInformationApi = new IndoorMapEntityInformationApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_indoorMapFloorOutlineInformationApi = new IndoorMapFloorOutlineInformationApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
        this.m_labelApi = new LabelApi(m_nativeRunner, m_uiRunner, m_eegeoMapApiPtr);
    }

    @WorkerThread
    void initialise(@NonNull EegeoMapOptions eegeoMapOptions) {
        m_cameraPosition = new CameraPosition.Builder(eegeoMapOptions.getCamera()).build();
        initLocation(m_cameraPosition);
    }

    /**
     * Moves the camera change from its current position to a new position.
     *
     * @param update Specifies the new position, either by specifying the new camera position, or by describing the desired display area.
     */
    @UiThread
    public void moveCamera(@NonNull final CameraUpdate update) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_cameraApi.moveCamera(update);
            }
        });
    }

    /**
     * Animates the camera change from its current position to a new position.
     *
     * @param update     Specifies the new position, either by specifying the new camera position, or by describing the desired display area.
     * @param durationMs Length of time for the transition, in ms.
     */
    @UiThread
    public void animateCamera(@NonNull final CameraUpdate update, final int durationMs) {
        final CameraAnimationOptions animationOptions = new CameraAnimationOptions.Builder()
                .duration(durationMs / 1000.0)
                .build();

        animateCamera(update, animationOptions);

    }

    /**
     * Animates the camera change from its current position to a new position.
     *
     * @param update Specifies the new position, either by specifying the new camera position, or by describing the desired display area.
     * @param animationOptions The animation options for the camera transition.
     */
    @UiThread
    public void animateCamera(@NonNull final CameraUpdate update, final CameraAnimationOptions animationOptions) {

        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_cameraApi.animateCamera(update, animationOptions);
            }
        });
    }




    @WorkerThread
    private void initLocation(@NonNull CameraPosition cameraPosition) {
        m_cameraApi.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                m_cameraApi.cancelAnimation();
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
     * Enable or Disable the camera restriction when viewing Indoor Maps. When enabled,
     * the camera is unable to move outside the bounds of the Indoor Map.
     * @param indoorCameraRestriction Whether the restriction is enabled or disabled.
     */
    @UiThread
    public void setIndoorCameraRestriction(final Boolean indoorCameraRestriction) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_cameraApi.setIndoorCameraRestriction(indoorCameraRestriction);
            }
        });
    }

    /**
     * Set the camera to focus on a particular screen coordinate, so it will rotate and zoom
     * around this point on screen.
     * @param point The screen space point, in units of pixels with the origin at the top left
     *              corner of the screen.
     */
    @UiThread
    public void setCameraScreenSpaceOffset(@NonNull final Point point) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_cameraApi.setCameraScreenSpaceOffset(point.x, point.y);
            }
        });
    }

    /**
     * Disable any previously set screen space offset set by setCameraScreenSpaceOffset amd
     * resume the default behavior.
     */
    @UiThread
    public void disableCameraScreenSpaceOffset() {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_cameraApi.disableCameraScreenSpaceOffset();
            }
        });
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
    private void jniSetCameraPosition(final CameraPosition cameraPosition) {
        m_uiRunner.runOnUiThread(new Runnable() {
            @UiThread
            @Override
            public void run() {
                m_cameraPosition = cameraPosition;
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
                m_transitioningToIndoorMap = false;
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
     * Registers a listener for indoor map enter failed events.
     *
     * @param listener The listener to be notified when the user fails to enter an indoor map.
     */
    @UiThread
    public void addOnIndoorEnterFailedListener(@NonNull OnIndoorEnterFailedListener listener) {
        m_onIndoorEnterFailedListeners.add(listener);
    }

    /**
     * Unregisters a listener for indoor map failed events.
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeOnIndoorEnterFailedListener(@NonNull OnIndoorEnterFailedListener listener) {
        m_onIndoorEnterFailedListeners.remove(listener);
    }

    /**
     * Registers a listener for indoor map loaded events.
     *
     * @param listener The listener to be notified when an indoor map is loaded.
     */
    @UiThread
    public void addOnIndoorMapLoadedListener(@NonNull OnIndoorMapLoadedListener listener) {
        m_onIndoorMapLoadedListeners.add(listener);
    }

    /**
     * Unregisters a listener for indoor map unloaded events.
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeOnIndoorMapLoadedListener(@NonNull OnIndoorMapLoadedListener listener) {
        m_onIndoorMapLoadedListeners.remove(listener);
    }

    /**
     * Registers a listener for indoor map unloaded events.
     *
     * @param listener The listener to be notified when an indoor map is unloaded.
     */
    @UiThread
    public void addOnIndoorMapUnloadedListener(@NonNull OnIndoorMapUnloadedListener listener) {
        m_onIndoorMapUnloadedListeners.add(listener);
    }

    /**
     * Unregisters a listener for indoor map unloaded events.
     *
     * @param listener The listener to be removed.
     */
    @UiThread
    public void removeOnIndoorMapUnloadedListener(@NonNull OnIndoorMapUnloadedListener listener) {
        m_onIndoorMapUnloadedListeners.remove(listener);
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
    public boolean enterIndoorMap(final String indoorMapId) {
        if (getActiveIndoorMap() != null || m_transitioningToIndoorMap) {
            return false;
        }

        if (!m_indoorMapEntryMarkerLocations.containsKey(indoorMapId)) {
            return false;
        }

        m_transitioningToIndoorMap = true;
        LatLngAlt location = m_indoorMapEntryMarkerLocations.get(indoorMapId);

        CameraPosition position = new CameraPosition.Builder()
                .target(location.latitude,  location.longitude)
                .indoor(indoorMapId)
                .zoom(19)
                .build();
        CameraAnimationOptions animationOptions = new CameraAnimationOptions.Builder()
                .interruptByGestureAllowed(false)
                .build();
        animateCamera(CameraUpdateFactory.newCameraPosition(position), animationOptions);
        return true;
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
    private void jniOnIndoorEnterFailed(final String indoorMapId) {
        m_uiRunner.runOnUiThread(new Runnable() {
            public void run() {
                m_indoorMap = null;
                m_currentIndoorFloor = -1;
                m_transitioningToIndoorMap = false;
                for (OnIndoorEnterFailedListener listener : m_onIndoorEnterFailedListeners) {
                    listener.OnIndoorEnterFailed(indoorMapId);
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

    @UiThread
    public void setExitIndoorWhenTooFarAway(final Boolean exitWhenFarAway) {
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                IndoorsApiJniCalls.setExitIndoorWhenTooFarAway(m_eegeoMapApiPtr, exitWhenFarAway);
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
     * Create a heatmap and add it to the map.
     *
     * @param heatmapOptions Creation parameters for the marker
     * @return The Heatmap that was added
     */
    @UiThread
    public Heatmap addHeatmap(@NonNull final HeatmapOptions heatmapOptions) {

        return new Heatmap(m_heatmapApi, heatmapOptions);
    }


    /**
     * Remove a heatmap from the map and destroy it.
     *
     * @param heatmap The Heatmap to remove.
     */
    @UiThread
    public void removeHeatmap(@NonNull final Heatmap heatmap) {

        heatmap.destroy();
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
     * Create a prop and add it to the map.
     *
     * @param propOptions Creation parameters for the prop
     * @return The Prop that was added
     */
    @UiThread
    public Prop addProp(@NonNull final PropOptions propOptions) {
        return new Prop(m_propsApi, propOptions);
    }


    /**
     * Remove a prop from the map and destroy it.
     *
     * @param prop The Prop to remove.
     */
    @UiThread
    public void removeProp(@NonNull final Prop prop) {
        prop.destroy();
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
     * Sets highlights on a list of indoor map entities to the specified ARGB color.
     *
     * @param indoorMapId ID for the indoor map the entities are in.
     * @param indoorEntityIds List of indoor map entities to highlight.
     * @param highlightColorARGB ARGB color for the highlight
     */
    public void setIndoorEntityHighlights(final String indoorMapId, final Collection<String> indoorEntityIds, final int highlightColorARGB)
    {
        setIndoorEntityHighlights(indoorMapId, indoorEntityIds, highlightColorARGB, 0.5f);
    }

    /**
     * Sets highlights on a list of indoor map entities to the specified ARGB color and border thickness.
     * Note: Border thickness only applies to area highlights.
     *
     * @param indoorMapId ID for the indoor map the entities are in.
     * @param indoorEntityIds List of indoor map entities to highlight.
     * @param highlightColorARGB ARGB color for the highlight
     * @param highlightBorderThickness A value between 0.0f and 1.0f that describes how thick the border should be. Defaults to 0.5f.
     */
    public void setIndoorEntityHighlights(final String indoorMapId, final Collection<String> indoorEntityIds, final int highlightColorARGB, final float highlightBorderThickness)
    {
        List<String> indoorEntityIdsList = new ArrayList<String>(indoorEntityIds);
        m_indoorEntityApi.setIndoorEntityHighlights(indoorMapId, indoorEntityIdsList, highlightColorARGB, highlightBorderThickness);
    }

    /**
     * Clears the highlights from a list of indoor map entities.
     */
    public void clearIndoorEntityHighlights(final String indoorMapId, final Collection<String> indoorEntityIds)
    {
        List<String> indoorEntityIdsList = new ArrayList<String>(indoorEntityIds);
        m_indoorEntityApi.clearIndoorEntityHighlights(indoorMapId, indoorEntityIdsList);
    }

    /**
     * Clears all indoor entity highlights.
     */
    public void clearAllIndoorEntityHighlights()
    {
        m_indoorEntityApi.clearAllIndoorEntityHighlights();
    }


    /**
     * Adds an IndoorMapEntityInformation object, that will become populated with the ids
     * of any indoor map entities belonging to the specified indoor map as map tiles stream in.
     * @param indoorMapId The id of the indoor map to obtain entity information for.
     * @param indoorMapEntityInformationChangedListener A listener object to obtain notification
     *                                                  when the IndoorMapEntityInformation has been
     *                                                  updated with indoor map entity ids.
     * @return The IndoorMapEntityInformation instance.
     */
    public IndoorMapEntityInformation addIndoorMapEntityInformation(
        @NonNull final String indoorMapId,
        final OnIndoorMapEntityInformationChangedListener indoorMapEntityInformationChangedListener
        )
    {
        return new IndoorMapEntityInformation(m_indoorMapEntityInformationApi, indoorMapId, indoorMapEntityInformationChangedListener);
    }

    /**
     * Remove an IndoorMapEntityInformation object, previously added via addIndoorMapEntityInformation.
     * @param indoorMapEntityInformation The IndoorMapEntityInformation instance to remove.
     */
    public void removeIndoorMapEntityInformation(@NonNull final IndoorMapEntityInformation indoorMapEntityInformation) {

        indoorMapEntityInformation.destroy();
    }

    /**
     * Adds an IndoorMapFloorOutlineInformation object, that will become populated with the outline
     * of the specified indoor map floor as map tiles stream in.
     * @param indoorMapId The id of the indoor map.
     * @param indoorMapFloorId The id of the indoor map floor to obtain outline information for.
     * @param indoorMapFloorOutlineInformationLoadedListener A listener object to obtain notification
     *                                                  when the IndoorMapFloorOutlineInformation has been
     *                                                  updated with outline.
     * @return The IndoorMapFloorOutlineInformation instance.
     */
    @UiThread
    public IndoorMapFloorOutlineInformation addIndoorMapFloorOutlineInformation(
            @NonNull final String indoorMapId,
            final int indoorMapFloorId,
            final OnIndoorMapFloorOutlineInformationLoadedListener indoorMapFloorOutlineInformationLoadedListener
            )
    {
        return new IndoorMapFloorOutlineInformation(m_indoorMapFloorOutlineInformationApi, indoorMapId, indoorMapFloorId, indoorMapFloorOutlineInformationLoadedListener);
    }

    /**
     * Remove an IndoorMapFloorOutlineInformation object, previously added via addIndoorMapFloorOutlineInformation.
     * @param indoorMapFloorOutlineInformation The IndoorMapFloorOutlineInformation instance to remove.
     */
    @UiThread
    public void removeIndoorMapFloorOutlineInformation(@NonNull final IndoorMapFloorOutlineInformation indoorMapFloorOutlineInformation)
    {
        indoorMapFloorOutlineInformation.destroy();
    }

    /**
     * Creates and returns a PoiService for this map.
     *
     * @return A new PoiService object.
     */
    @UiThread
    public PoiService createPoiService() {
        PoiService poiService = new PoiService(m_poiApi);
        return poiService;
    }

    /**
     * Creates and returns a TagService for this map
     * @return A new TagService object.
     */
    @UiThread
    public TagService createTagService() {
        TagService tagService = new TagService(m_tagApi);
        return tagService;
    }

    /**
     * Creates and returns a MapsceneService for this map.
     *
     * @return A new MapsceneService object.
     */
    public MapsceneService createMapsceneService() {
        MapsceneService mapsceneService = new MapsceneService(m_mapsceneApi, this);
        return mapsceneService;
    }

    /**
     * Creates and returns a RoutingService for this map.
     *
     * @return A new RoutingService object.
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

    /**
     * Register a listener to an event raised when one or more indoor map entities are clicked or tapped by the user.
     *
     * @param listener the listener to add
     */
    @UiThread
    public void addOnIndoorEntityPickedListener(@NonNull OnIndoorEntityPickedListener listener) {
        m_indoorEntityApi.addOnIndoorEntityPickedListener(listener);
    }

    /**
     * Unregister a listener to an event raised when one or more indoor map entities are clicked or tapped by the user.
     *
     * @param listener the listener to remove
     */
    @UiThread
    public void removeOnIndoorEntityPickedListener(@NonNull OnIndoorEntityPickedListener listener) {
        m_indoorEntityApi.removeOnIndoorEntityPickedListener(listener);
    }

    /**
     * Begin an operation to precache a spherical area of the map. This allows that area to load
     * faster in future.
     *
     * @param center The center of the area to precache.
     * @param radius The radius (in meters) of the area to precache.
     * @param callback The function to call when the precache operation completes. The function will
     *                be passed a boolean indicating whether the precache completed successfully.
     *
     * @return an object with a cancel() method to allow you to cancel the precache operation.
     */
    @UiThread
    public PrecacheOperation precache(
            final LatLng center,
            final double radius,
            final OnPrecacheOperationCompletedListener callback) throws IllegalArgumentException {
        final double maximumPrecacheRadius = m_precacheApi.getMaximumPrecacheRadius();

        if (radius < 0.0 || radius > maximumPrecacheRadius)
        {
            throw new IllegalArgumentException(
                    String.format("radius %f outside of valid (0, %f] range.",
                            radius, maximumPrecacheRadius));
        }

        return m_precacheApi.precache(center, radius, callback);
    }

    /**
     * Gets the maximum radius value that can be passed to precache(center, radius, callback)
     *
     * @return the maxium radius that may be passed to precache, in meters
     */
    @UiThread
    public double getMaximumPrecacheRadius() {
        return m_precacheApi.getMaximumPrecacheRadius();
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
    private void jniOnMapsceneRequestCompleted(final int mapsceneRequestId, final boolean succeeded, final Mapscene mapscene) {
        m_mapsceneApi.notifyRequestComplete(mapsceneRequestId, succeeded, mapscene);
    }

    @WorkerThread
    private void jniOnSearchTagsLoaded() {
        m_tagApi.notifyTagsLoaded();
    }

    @WorkerThread
    private void jniOnPrecacheQueryCompleted(final int precacheOperationId, PrecacheOperationResult result) {
        m_precacheApi.notifyPrecacheOperationComplete(precacheOperationId, result);
    }

    @WorkerThread
    private void jniOnRoutingQueryCompleted(final int routingQueryId, RoutingQueryResponse response) {
        m_routingApi.notifyQueryComplete(routingQueryId, response);
    }

    @WorkerThread
    private void jniOnIndoorEntityPicked(IndoorEntityPickedMessage message) {
        m_indoorEntityApi.notifyIndoorEntityPicked(message);
    }

    @WorkerThread
    private void jniOnIndoorMapEntityInformationChanged(final int indoorMapEntityInformationId) {
        m_indoorMapEntityInformationApi.notifyIndoorMapEntityInformationChanged(indoorMapEntityInformationId);
    }

    @WorkerThread
    private void jniOnIndoorMapFloorOutlineInformationLoaded(final int indoorMapFloorOutlineInformationId) {
        m_indoorMapFloorOutlineInformationApi.notifyIndoorMapFloorOutlineInformationLoaded(indoorMapFloorOutlineInformationId);
    }

    @WorkerThread
    private void jniOnIndoorMapLoaded(final String indoorMapId) {
        m_uiRunner.runOnUiThread(new Runnable() {
            public void run() {
                for (OnIndoorMapLoadedListener listener : m_onIndoorMapLoadedListeners) {
                    listener.onIndoorMapLoaded(indoorMapId);
                }
            }
        });
    }

    @WorkerThread
    private void jniOnIndoorMapUnloaded(final String indoorMapId) {
        m_uiRunner.runOnUiThread(new Runnable() {
            public void run() {
                for (OnIndoorMapUnloadedListener listener : m_onIndoorMapUnloadedListeners) {
                    listener.onIndoorMapUnloaded(indoorMapId);
                }
            }
        });
    }

    @WorkerThread
    private void jniOnIndoorEntryMarkerAdded(final String indoorMapId, final LatLngAlt location) {
        m_uiRunner.runOnUiThread(new Runnable() {
            public void run() {
                m_indoorMapEntryMarkerLocations.put(indoorMapId, location);
            }
        });
    }

    @WorkerThread
    private void jniOnIndoorEntryMarkerRemoved(final String indoorMapId) {
        m_uiRunner.runOnUiThread(new Runnable() {
            public void run() {
                m_indoorMapEntryMarkerLocations.remove(indoorMapId);
            }
        });
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

    /**
     * Retrieve information about the closest point on a Route to a given input point.
     *
     * @param point The input point to find the closest point on the Route with.
     * @param route The Route that should be tested against.
     * @param pointOnRouteOptions Additional options for the Route; e.g, Indoor Map Id.
     */
    @UiThread
    public Promise<PointOnRoute> getPointOnRoute(LatLng point, Route route, PointOnRouteOptions pointOnRouteOptions)
    {
        return m_pathApi.getPointOnRoute(point, route, pointOnRouteOptions);
    }


    /**
     * Retrieve information about the closest point on a Path to a given input point.
     *
     * @param point The input point to find the closest point on the Path with.
     * @param path The Path that should be tested against.
     */
    @UiThread
    public Promise<PointOnPath> getPointOnPath(LatLng point, List<LatLng> path)
    {
        return m_pathApi.getPointOnPath(point, path);
    }


    /**
     * Make labels visible or invisible.
     *
     * @param enabled Whether labels should be visible.
     */
    @WorkerThread
    public void setLabelsEnabled(boolean enabled)
    {
        m_labelApi.setLabelsEnabled(enabled);
    }


    public static final class AllowApiAccess {
        @WorkerThread
        private AllowApiAccess() {
        }
    }
}

