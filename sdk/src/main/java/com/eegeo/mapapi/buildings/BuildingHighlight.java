package com.eegeo.mapapi.buildings;


import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.util.NativeApiObject;

import java.util.concurrent.Callable;

public class BuildingHighlight extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final BuildingsApi m_buildingsApi;

    private int m_colorARGB;
    private boolean m_shouldCreateView;
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
        m_shouldCreateView = buildingHighlightOptions.getShouldCreateView();
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
                m_buildingsApi.unregister(BuildingHighlight.this, BuildingHighlight.m_allowHandleAccess);
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
