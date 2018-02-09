package com.wrld.widgets.searchbox.view;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.IOnSearchResultListener;
import com.wrld.widgets.searchbox.model.ISearchResult;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchWidgetSuggestionModel;

import java.util.List;

public class SuggestionResultsController implements AdapterView.OnItemClickListener, IOnSearchResultListener, View.OnFocusChangeListener {

    private final SuggestionResultsAdapter m_adapter;
    private View m_viewRoot;
    private ListView m_listView;

    private SearchView m_searchView;

    private SearchWidgetSuggestionModel m_model;
    private SearchResultsModel m_suggestionResults;
    private boolean m_searchViewHasFocus;

    public SuggestionResultsController(SearchWidgetSuggestionModel model,
                                       SearchResultsModel suggestionResults,
                                       View viewContainer,
                                       SearchView searchView) {
        m_model = model;

        m_suggestionResults = suggestionResults;
        m_suggestionResults.addResultListener(this);

        m_viewRoot = viewContainer;
        m_listView = (ListView)viewContainer.findViewById(R.id.searchbox_autocomplete_list);
        // TODO pass in preview count.
        m_adapter = new SuggestionResultsAdapter(m_suggestionResults, m_model, LayoutInflater.from(m_viewRoot.getContext()), 3);

        m_searchViewHasFocus = false;

        m_listView.setAdapter(m_adapter);
        m_listView.setOnItemClickListener(this);

        m_searchView = searchView;
        m_searchView.setOnQueryTextFocusChangeListener(this);

        updateVisibility();
    }

    public void clean() {
        m_suggestionResults.removeResultListener(this);
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
        if(m_suggestionResults.getCurrentQueryResults() != null &&
           m_suggestionResults.getTotalCurrentQueryResults() > 0 && m_searchViewHasFocus)
        {
            m_viewRoot.setVisibility(View.VISIBLE);
            return;
        }
        m_viewRoot.setVisibility(View.GONE);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        m_searchViewHasFocus = hasFocus;
        updateVisibility();
    }
}
