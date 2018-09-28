package com.eegeo.mapapi.indoorentities;

import java.util.ArrayList;

/**
 * This message contains information about one or more indoor map entities that have been clicked or tapped.
 * It is received when the users clicks or taps on an indoor map entity.
 */
public class IndoorEntityPickedMessage
{
    private float m_screenPointX;
    private float m_screenPointY;
    private ArrayList<String> m_indoorMapEntityIds;
    private String m_indoorMapId;

    IndoorEntityPickedMessage(float screenPointX, float screenPointY, ArrayList<String> indoorMapEntityIds, String indoorMapId) {
        this.m_screenPointX = screenPointX;
        this.m_screenPointY = screenPointY;
        this.m_indoorMapEntityIds = indoorMapEntityIds;
        this.m_indoorMapId = indoorMapId;
    }

    /**
     * @return A float containing the x-position of the click or tap in screen space.
     */
    public float screenPointX() {
        return m_screenPointX;
    }

    /**
     * @return A float containing the y-position of the click or tap in screen space.
     */
    public float screenPointY() {
        return m_screenPointY;
    }

    /**
     * @return An ArrayList of indoor map entity id strings, representing which map entities were clicked or tapped.
     */
    public ArrayList<String> indoorMapEntityIds() {
        return m_indoorMapEntityIds;
    }

    /**
     * @return The id of the indoor map that the clicked/tapped entities belong to.
     */
    public String indoorMapId() {
        return m_indoorMapId;
    }
}
