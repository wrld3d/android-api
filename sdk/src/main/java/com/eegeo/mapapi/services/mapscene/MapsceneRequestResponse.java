package com.eegeo.mapapi.services.poi;

import com.eegeo.mapapi.services.mapscene.Mapscene;

import java.util.List;


/**
 * A response to a Mapscene Request. Returned when a mapscene request completes via callback.
 */
public class MapsceneRequestResponse {

    boolean m_succeeded;
    Mapscene m_mapscene;

    public MapsceneRequestResponse(boolean succeeded, Mapscene mapscene) {
        this.m_succeeded = succeeded;
        this.m_mapscene = mapscene;
    }

    /**
     * @return A boolean indicating whether the request succeeded or not.
     */
    public boolean succeeded() {
        return m_succeeded;
    }

    /**
     * Get the requested Mapscene.
     * @return The search results. This will be null if the request failed
     */
    public Mapscene getMapscene() {
        return m_mapscene;
    }
}

