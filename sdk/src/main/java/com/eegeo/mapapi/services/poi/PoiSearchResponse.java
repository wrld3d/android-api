package com.eegeo.mapapi.services.poi;

import java.util.List;


/**
 * A response to a POI search. Returned when a search completes via callback.
 */
public class PoiSearchResponse {

    boolean m_succeeded;
    List<PoiSearchResult> m_results;


    PoiSearchResponse(boolean succeeded, List<PoiSearchResult> results) {
        this.m_succeeded = succeeded;
        this.m_results = results;
    }

    /**
     * @return A boolean indicating whether the search succeeded or not.
     */
    public boolean succeeded() {
        return m_succeeded;
    }

    /**
     * Get the results of the search as a List of PoiSearchResult objects.
     * @return The search results. This will be empty if the search failed, or if no POIs were found.
     */
    public List<PoiSearchResult> getResults() {
        return m_results;
    }
}

