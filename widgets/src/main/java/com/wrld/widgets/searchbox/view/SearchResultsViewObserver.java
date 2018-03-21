package com.wrld.widgets.searchbox.view;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsViewObserver implements SearchResultsViewListener {

    private List<SearchResultsViewListener> m_listeners;

    public SearchResultsViewObserver() {
        m_listeners = new ArrayList<>();
    }

    public void addListener(SearchResultsViewListener listener) {
        if(listener != null) {
            m_listeners.add(listener);
        }
    }

    public void removeListener(SearchResultsViewListener listener) {
        if(listener != null && m_listeners.contains(listener)) {
            m_listeners.remove(listener);
        }
    }

    public void onSearchResultsShown() {
        for(SearchResultsViewListener listener : m_listeners) {
            listener.onSearchResultsShown();
        }
    }

    public void onSearchResultsHidden() {
        for(SearchResultsViewListener listener : m_listeners) {
            listener.onSearchResultsHidden();
        }
    }
}
