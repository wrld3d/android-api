package com.eegeo.mapapi.pointonpath;

public final class PointOnRouteOptions {

    private String m_indoorMapId = "";

    private int m_indoorMapFloorId = 0;

    public PointOnRouteOptions(
    ) {
    }

    /**
     * Sets the indoor map ID to allow the point to test against routes indoors.
     *
     * @param indoorMapId The ID of the indoor map.
     * @return The PointOnRouteOptions object on which the method was called, with the new indoorMapId set.
     */
    public PointOnRouteOptions indoorMapId(String indoorMapId) {
        m_indoorMapId = indoorMapId;
        return this;
    }

    /**
     * Sets the indoor map floor ID to specify the floor the point and route are on.
     *
     * @param indoorMapFloorId The floor ID of the indoor map.
     * @return The PointOnRouteOptions object on which the method was called, with the new indoorMapFloorId set.
     */
    public PointOnRouteOptions indoorMapFloorId(int indoorMapFloorId) {
        m_indoorMapFloorId = indoorMapFloorId;
        return this;
    }

    String getIndoorMapId() { return m_indoorMapId; }

    int getIndoorMapFloorId() { return m_indoorMapFloorId; }

}
