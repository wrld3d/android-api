package com.wrld.widgets.searchbox.view;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.IOnSearchResultListener;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;

import java.util.List;

public class SearchResultsController implements IOnSearchResultListener, AdapterView.OnItemClickListener {

    private final SearchWidgetSearchModel m_model;
    private final SearchResultsModel m_searchResultsModel;
    private final SearchResultsModel m_suggestionResultsModel;
    private final View m_viewRoot;
    private final ListView m_listView;
    private final SearchResultsAdapter m_adapter;

    public SearchResultsController(SearchWidgetSearchModel searchModel,
                                   SearchResultsModel searchResultsModel,
                                   SearchResultsModel suggestionResultsModel,
                                   View viewRoot)
    {
        m_model = searchModel;
        m_searchResultsModel = searchResultsModel;
        m_searchResultsModel.addResultListener(this);

        m_suggestionResultsModel = suggestionResultsModel;
        m_suggestionResultsModel.addResultListener(this);

        // TODO Pass the 3 in.
        m_adapter = new SearchResultsAdapter(m_searchResultsModel, m_model, LayoutInflater.from(viewRoot.getContext()), 3);

        m_viewRoot = viewRoot;
        m_listView = (ListView)m_viewRoot.findViewById(R.id.searchbox_search_results_list);
        m_listView.setOnItemClickListener(this);
        m_listView.setAdapter(m_adapter);

        updateVisibility();
    }

    public void clean() {
        m_searchResultsModel.removeResultListener(this);
        m_suggestionResultsModel.removeResultListener(this);
    }

    @Override
    public void onSearchResultsRecieved(SearchQuery query, List<SearchProviderQueryResult> results) {
        updateVisibility();
        m_adapter.refresh(true);
    }

    @Override
    public void onSearchResultsCleared() {
        updateVisibility();
        m_adapter.refresh(false);
    }

    private void updateVisibility() {
        boolean hasSuggestionResults = m_suggestionResultsModel.getTotalCurrentQueryResults() > 0;
        boolean hasSearchResults = m_searchResultsModel.getTotalCurrentQueryResults() > 0;
        if(!hasSuggestionResults && hasSearchResults) {
            m_viewRoot.setVisibility(View.VISIBLE);
        }
        else {
            m_viewRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Pair<Integer, Integer> providerIndex = m_adapter.getProviderIndex(position);
        SearchProviderQueryResult result = m_searchResultsModel.getCurrentQueryResults().get(providerIndex.first);
        if(result != null) {
            boolean selectedFooter = m_adapter.isFooter(position);
            if(selectedFooter) {
                m_adapter.toggleState(result.getProviderId());
            }
            else {
                // search result callback.
            }
        }
    }
}
