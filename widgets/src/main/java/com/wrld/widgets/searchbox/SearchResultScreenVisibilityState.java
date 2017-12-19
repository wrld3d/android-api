package com.wrld.widgets.searchbox;

import android.view.View;

import com.wrld.widgets.ui.UiScreenMemento;

public class SearchResultScreenVisibilityState implements UiScreenMemento<SearchResultScreenVisibilityState> {

    private int m_resultsVisibility;
    private int m_autocompleteVisibility;

    public SearchResultScreenVisibilityState getState() {return this;}

    public SearchResultScreenVisibilityState(int resultsVisibility, int autocompleteVisibility){
        m_resultsVisibility = resultsVisibility;
        m_autocompleteVisibility = autocompleteVisibility;
    }

    public void apply(View resultsView, View autocompleteView){
        resultsView.setVisibility(m_resultsVisibility);
        autocompleteView.setVisibility(m_autocompleteVisibility);
    }
}
