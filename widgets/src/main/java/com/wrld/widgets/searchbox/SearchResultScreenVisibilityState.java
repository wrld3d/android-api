package com.wrld.widgets.searchbox;

import android.view.View;

import com.wrld.widgets.ui.UiScreenController.ScreenState;
import com.wrld.widgets.ui.UiScreenMemento;

class SearchResultScreenVisibilityState implements UiScreenMemento<SearchResultScreenVisibilityState> {

    private int m_resultsVisibility;
    private int m_autocompleteVisibility;
    private ScreenState m_screenState;

    public SearchResultScreenVisibilityState getState() {return this;}

    public ScreenState getScreenState() { return m_screenState; }

    public SearchResultScreenVisibilityState(int resultsVisibility,
                                             int autocompleteVisibility,
                                             ScreenState screenState){
        m_resultsVisibility = resultsVisibility;
        m_autocompleteVisibility = autocompleteVisibility;
        m_screenState = screenState;
    }

    public void apply(View resultsView, View autocompleteView){
        resultsView.setVisibility(m_resultsVisibility);
        autocompleteView.setVisibility(m_autocompleteVisibility);
    }
}
