package com.wrld.widgets.searchbox.view;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.wrld.widgets.searchbox.model.IOnSearchResultListener;
import com.wrld.widgets.searchbox.model.ISearchResult;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchWidgetSuggestionModel;

import java.util.List;

public class SuggestionResultsController implements AdapterView.OnItemClickListener, IOnSearchResultListener, View.OnFocusChangeListener {

    private final SuggestionResultsAdapter m_adapter;
    private ListView m_view;

    private SearchView m_searchView;

    private SearchWidgetSuggestionModel m_model;
    private boolean m_searchViewHasFocus;

    public SuggestionResultsController(SearchWidgetSuggestionModel model,
                                       ListView view,
                                       SearchView searchView) {
        m_model = model;
        m_model.setResultListener(this);

        m_view = view;
        m_adapter = new SuggestionResultsAdapter(m_model, LayoutInflater.from(view.getContext()), 3);

        m_searchViewHasFocus = false;

        m_view.setAdapter(m_adapter);
        m_view.setOnItemClickListener(this);

        m_searchView = searchView;
        m_searchView.setOnQueryTextFocusChangeListener(this);

        updateVisibility();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ISearchResult result = (ISearchResult)m_adapter.getItem(position);
        if(result != null)
        {
            String searchTerm = result.getTitle();
            m_searchView.setQuery(searchTerm, true);
        }
    }

    @Override
    public void onSearchResultsRecieved(final SearchQuery query, final List<SearchProviderQueryResult> results) {
        m_adapter.notifyDataSetChanged();
        updateVisibility();
    }


    @Override
    public void onSearchResultsCleared() {
        m_adapter.notifyDataSetChanged();
        updateVisibility();
    }

    private void updateVisibility() {
        if(m_model.getCurrentQueryResults() != null && m_model.getTotalCurrentQueryResults() > 0 && m_searchViewHasFocus)
        {
            m_view.setVisibility(View.VISIBLE);
            return;
        }
        m_view.setVisibility(View.GONE);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        m_searchViewHasFocus = hasFocus;
        updateVisibility();
    }
}
