package com.eegeo.mapapi.indoorentities;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * This message contains information about one or more indoor map entities that have been clicked or tapped.
 * It is received when the user clicks or taps on an indoor map entity.
 */
public class IndoorEntityPickedMessage
{
    /**
     * The x-position of the click or tap in screen space.
     */
    public final double screenPointX;

    /**
     * The y-position of the click or tap in screen space.
     */
    public final double screenPointY;

    /**
     * An ArrayList of indoor map entity id strings, representing which map entities were clicked or tapped.
     */
    public final ArrayList<String> indoorMapEntityIds;

    /**
     * The id of the indoor map that the clicked/tapped entities belong to.
     */
    public final String indoorMapId;

    /**
     * @eegeo.internal
     */
    IndoorEntityPickedMessage(@NonNull double screenPointX,
                              @NonNull double screenPointY,
                              @NonNull ArrayList<String> indoorMapEntityIds,
                              @NonNull String indoorMapId) {
        this.screenPointX = screenPointX;
        this.screenPointY = screenPointY;
        this.indoorMapEntityIds = indoorMapEntityIds;
        this.indoorMapId = indoorMapId;
    }

}
