package com.eegeo.mapapi.services.poi;

import java.util.List;


public class PoiSearchResults {

    boolean m_succeeded;
    List<PoiSearchResult> m_results;


    PoiSearchResults(boolean succeeded, List<PoiSearchResult> results) {
        this.m_succeeded = succeeded;
        this.m_results = results;
    }

    public boolean succeeded() {
        return m_succeeded;
    }

    public List<PoiSearchResult> getResults() {
        return m_results;
    }
}

