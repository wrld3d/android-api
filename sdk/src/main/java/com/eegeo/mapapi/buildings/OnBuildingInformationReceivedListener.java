package com.eegeo.mapapi.buildings;

import android.support.annotation.UiThread;

public interface OnBuildingInformationReceivedListener {
    @UiThread
    void onBuildingInformationReceived(BuildingHighlight buildingHighlight);

}
