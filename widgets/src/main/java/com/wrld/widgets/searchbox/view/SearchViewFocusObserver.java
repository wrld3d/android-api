package com.wrld.widgets.searchbox.view;

import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchViewFocusObserver implements View.OnFocusChangeListener {

    private final SearchView m_view;
    private boolean m_hasFocus;
    List<View.OnFocusChangeListener> m_listeners;

    public SearchViewFocusObserver(SearchView view) {
        m_view = view;
        m_listeners = new ArrayList<>();
        m_hasFocus = m_view.hasFocus();
        m_view.setOnQueryTextFocusChangeListener(this);
    }

    public void addListener(View.OnFocusChangeListener listener) {
        m_listeners.add(listener);
    }

    public void removeListener(View.OnFocusChangeListener listener) {
        m_listeners.remove(listener);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        m_hasFocus = hasFocus;
        for(View.OnFocusChangeListener listener : m_listeners) {
            listener.onFocusChange(view, hasFocus);
        }
    }

    public boolean hasFocus() {
        return m_hasFocus;
    }
}
