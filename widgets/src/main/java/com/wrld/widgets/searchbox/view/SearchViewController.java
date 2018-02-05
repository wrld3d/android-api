package com.wrld.widgets.searchbox.view;


import android.text.TextUtils;
import android.widget.SearchView;

import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;

public class SearchViewController implements SearchView.OnQueryTextListener {

    SearchView m_view;
    SearchWidgetSearchModel m_model;

    public SearchViewController(SearchWidgetSearchModel model,
                                SearchView view)
    {
        // Split model listener into different listeners.
        //m_model.setListener(this);
        m_model = model;

        m_view = view;
        m_view.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        m_view.clearFocus();
        if(!TextUtils.isEmpty(s)) {
            m_model.doSearch(s);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(!s.isEmpty())
        {
            m_model.doSuggestions(s);
        }
        else
        {
            m_model.clear();
        }
        return false;
    }
}
