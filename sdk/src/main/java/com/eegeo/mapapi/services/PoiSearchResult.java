package com.eegeo.mapapi.services;


public class PoiSearchResult {

    boolean m_succeeded;
    String m_resultJson;
    String m_errorString;


    PoiSearchResult(boolean succeeded, String results) {
        this.m_succeeded = succeeded;
        this.m_resultJson = (succeeded) ? results : null;
        this.m_errorString = (succeeded) ? null : results;
    }

    public boolean succeeded() {
        return m_succeeded;
    }

    public String getResultJson() {
        return m_resultJson;
    }

    public String getErrorString() {
        return m_errorString;
    }
}

