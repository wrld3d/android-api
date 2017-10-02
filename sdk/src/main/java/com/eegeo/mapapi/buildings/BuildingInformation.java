package com.eegeo.mapapi.buildings;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

/**
 * Information about a building on the map, obtained by adding a BuildingHighlight object and
 * to the map.
 */
public class BuildingInformation {
    /**
     * A unique identifier for the building. The BuildingId for a building on the map is not
     * necessarily maintained between versions of the map or Api.
     */
    public final String buildingId;

    /**
     * Summary information about the dimensions of the building.
     */
    public final BuildingDimensions buildingDimensions;

    /**
     * An array of BuildingContour objects, representing the geometry of the building.
     */
    public final BuildingContour contours[];

    /**
     * @eegeo.internal
     */
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
