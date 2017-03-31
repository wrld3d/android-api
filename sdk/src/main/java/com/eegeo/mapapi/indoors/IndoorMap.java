package com.eegeo.mapapi.indoors;

import android.support.annotation.UiThread;

/**
 * Encapsulates a set of immutable properties pertaining to an indoor map.  These properties are
 * set through the eeGeo indoor map service, and cannot be changed through the Android SDK.
 *
 * An IndoorMap object can be obtained from an EegeoMap object through the getActiveIndoorMap method.
 */
public class IndoorMap {
    /**
     * Gets the unique identifier for the indoor map.
     */
    public final String id;
    /**
     * Gets a readable name for the indoor map, usually the building name.
     */
    public final String name;
    /**
     * Gets the number of floors in the indoor map.
     */
    public final int floorCount;
    /**
     * Gets an array of floor identifiers, suitable for display. These are generally string versions of
     * floor numbers or other short identifiers such as "G" or "LG".
     */
    public final String[] floorIds;
    /**
     * Gets an array of floor names. Floor names may be longer than floor ids.
     */
    public final String[] floorNames;
    /**
     * Gets an array of floor numbers.
     */
    public final int[] floorNumbers;
    /**
     * Gets user data which has been associated with the map through the indoor map service.
     * The user data is a string in JSON format.
     */
    public final String userData;
    /**
     * Currently unused.
     *
     * @eegeo.internal
     */
    private String sourceVendor;

    /**
     * This constructor is for internal SDK use -- use EegeoMap.getActiveIndoorMap to
     * obtain a populated IndoorMap object.
     *
     * @eegeo.internal
     */
    @UiThread
    public IndoorMap(
            String indoorMapId,
            String indoorMapName,
            String indoorSourceVendor,
            int indoorFloorCount,
            String[] indoorFloorIds,
            String[] indoorFloorNames,
            int[] indoorFloorNumbers,
            String indoorUserData
    ) {
        id = indoorMapId;
        name = indoorMapName;
        sourceVendor = indoorSourceVendor;
        floorCount = indoorFloorCount;
        floorIds = indoorFloorIds;
        floorNames = indoorFloorNames;
        floorNumbers = indoorFloorNumbers;
        userData = indoorUserData;
    }

}
