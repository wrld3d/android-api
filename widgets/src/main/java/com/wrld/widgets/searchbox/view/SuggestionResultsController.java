package com.wrld.widgets.searchbox.view;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrld.widgets.searchbox.model.ISearchResult;
import com.wrld.widgets.searchbox.model.ISearchWidgetModelListener;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;

import java.util.List;

public class SuggestionResultsController implements AdapterView.OnItemClickListener, ISearchWidgetModelListener {

    private final SuggestionResultsAdapter m_adapter;
    private ListView m_view;
    private SearchWidgetSearchModel m_model;

    public SuggestionResultsController(SearchWidgetSearchModel model, ListView view) {
        m_model = model;

        // Need more specific listeners - and abiliity to set multiple callbacks.
        m_model.setListener(this);

        m_view = view;
        m_adapter = new SuggestionResultsAdapter(m_model, LayoutInflater.from(view.getContext()), 3);

        m_view.setAdapter(m_adapter);
        m_view.setOnItemClickListener(this);

        updateVisibility();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ISearchResult result = (ISearchResult)m_adapter.getItem(position);
        if(result != null)
        {
            String searchTerm = result.getTitle();
            m_model.clear();
            m_model.doSearch(searchTerm);
        }

    }

    @Override
    public void onSearchQueryStarted(SearchQuery query) {

    }

    @Override
    public void onSearchQueryCompleted(SearchQuery query, List<SearchProviderQueryResult> results) {

    }

    @Override
    public void onSuggestionQueryStarted(SearchQuery query) {

    }

    @Override
    public void onSuggestionQueryCompleted(SearchQuery query, List<SearchProviderQueryResult> results) {
        m_adapter.notifyDataSetChanged();
        updateVisibility();
    }

    private void updateVisibility() {
        if(m_model.getCurrentQueryResults() != null && m_model.getTotalCurrentQueryResults() > 0)
        {
            if(m_model.getCurrentQuery().getQueryType() == SearchQuery.QueryType.Suggestion)
            {
                m_view.setVisibility(View.VISIBLE);
                return;
            }
        }
        Log.d("EEGEO", "Suggestions: Have " + m_model.getTotalCurrentQueryResults() + " results.");
        m_view.setVisibility(View.GONE);
    }

    @Override
    public void onSearchQueryCancelled() {
        m_adapter.notifyDataSetChanged();
        updateVisibility();
    }

    @Override
    public void onSearchResultsCleared() {
        m_adapter.notifyDataSetChanged();
        updateVisibility();
    }
}
