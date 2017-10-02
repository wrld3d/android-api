package com.eegeo.mapapi.buildings;

import android.support.annotation.UiThread;

/**
 * An interface that may be implemented and supplied to BuildingHighlightOptions in order to
 * receive notification that BuildingInformation has been received.
 */
public interface OnBuildingInformationReceivedListener {
    /**
     * Called when a BuildingHighlight object receives BuildingInformation. Access this with
     * buildingHighlight.getBuildingInformation().
     * @param buildingHighlight The BuildingHighlight object for which BuildingInformation has been received.
     */
    @UiThread
    void onBuildingInformationReceived(BuildingHighlight buildingHighlight);

}
