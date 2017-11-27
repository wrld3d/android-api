package com.eegeo.mapapi.buildings;


import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

/**
 * Represents a single selected building on the map,
 * for displaying a graphical overlay to highlight the building, or for obtaining information about
 * the building.
 *
 * <br>
 * <br>
 * Public methods in this class must be called on the Android UI thread.
 */
public class BuildingHighlight extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final BuildingsApi m_buildingsApi;

    private int m_colorARGB;
    private BuildingInformation m_buildingInformation = null;
    private OnBuildingInformationReceivedListener m_onBuildingInformationReceivedListener;


    /**
     * This constructor is for internal SDK use only -- use EegeoMap.addBuildingHighlight to create a highlight
     *
     * @eegeo.internal
     */
    @UiThread
    public BuildingHighlight(@NonNull final BuildingsApi buildingsApi,
                    @NonNull final BuildingHighlightOptions buildingHighlightOptions) {
        super(buildingsApi.getNativeRunner(), buildingsApi.getUiRunner(),
                new Callable<Integer>() {
                    @WorkerThread
                    @Override
                    public Integer call() throws Exception {
                        return buildingsApi.create(buildingHighlightOptions, m_allowHandleAccess);
                    }
                });

        m_buildingsApi = buildingsApi;
        m_colorARGB = buildingHighlightOptions.getColor();
        m_onBuildingInformationReceivedListener = buildingHighlightOptions.getOnBuildingInformationReceivedListener();

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_buildingsApi.register(BuildingHighlight.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Gets the color of the highlight.
     *
     * @return The color of the highlight as a 32-bit ARGB color.
     */
    @UiThread
    public int getColor() {
        return m_colorARGB;
    }

    /**
     * Sets the color for this highlight.
     *
     * @param color The color of the highlight as a 32-bit ARGB color.
     */
    @UiThread
    public void setColor(int color) {
        m_colorARGB = color;
        updateNativeStyleAttributes();
    }

    /**
     * Removes this highlight from the map and destroys it. Use EegeoMap.removeBuildingHighlight
     *
     * @eegeo.internal
     */
    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_buildingsApi.destroy(BuildingHighlight.this, BuildingHighlight.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    void setBuildingInformation(BuildingInformation buildingInformation) {
        this.m_buildingInformation = buildingInformation;
        if (m_onBuildingInformationReceivedListener != null) {
            m_onBuildingInformationReceivedListener.onBuildingInformationReceived(this);
        }
    }

    /**
     * Returns building information for the map building associated with this highlight, if available.
     * Returns null if the request for building information is still pending (internally, building
     * information may be fetched asynchronously).
     * Also returns null if no building information was successfully retrieved for this building
     * highlight. This may be either because no building exists at the query location supplied in
     * the BuildingHighlightOptions construction parameters, or because an internal web request failed.
     * @return the BuildingInformation associated with this highlight, or null.
     */
    @UiThread
    public BuildingInformation getBuildingInformation() {
        return this.m_buildingInformation;
    }


    @UiThread
    private void updateNativeStyleAttributes() {
        final int colorARGB = m_colorARGB;

        submit(new Runnable() {
            @WorkerThread
            public void run() {
                m_buildingsApi.setStyleAttributes(
                        getNativeHandle(),
                        BuildingHighlight.m_allowHandleAccess,
                        colorARGB);
            }
        });
    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by BuildingsApi");

        if (!hasNativeHandle())
            throw new IllegalStateException("Native handle not available");

        return getNativeHandle();
    }

    static final class AllowHandleAccess {
        @WorkerThread
        private AllowHandleAccess() {
        }
    }
}
