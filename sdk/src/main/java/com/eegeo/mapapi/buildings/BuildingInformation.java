package com.eegeo.mapapi.buildings;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

public class BuildingInformation {
    public final String buildingId;
    public final BuildingDimensions buildingDimensions;
    public final BuildingContour contours[];

    @UiThread
    public BuildingInformation(
            @NonNull String buildingId,
            @NonNull BuildingDimensions buildingDimensions,
            @NonNull BuildingContour contours[]
    ) {
        this.buildingId = buildingId;
        this.buildingDimensions = buildingDimensions;
        this.contours = contours;
    }

}
