package com.wrld.widgets.searchbox.view;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrld.widgets.searchbox.model.IOnSearchResultListener;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;

import java.util.List;

public class SearchResultsController implements IOnSearchResultListener, AdapterView.OnItemClickListener {

    private final SearchWidgetSearchModel m_searchModel;
    private final ListView m_view;
    private final SearchResultsAdapter m_adapter;

    public SearchResultsController(SearchWidgetSearchModel searchModel, ListView view)
    {
        m_searchModel = searchModel;
        m_searchModel.setResultListener(this);

        m_adapter = new SearchResultsAdapter(m_searchModel, LayoutInflater.from(view.getContext()), 3);

        m_view = view;
        m_view.setOnItemClickListener(this);
        m_view.setAdapter(m_adapter);
    }

    @Override
    public void onSearchResultsRecieved(SearchQuery query, List<SearchProviderQueryResult> results) {
        updateVisibility();
        m_adapter.refresh();
    }

    @Override
    public void onSearchResultsCleared() {
        updateVisibility();
        m_adapter.refresh();
    }

    private void updateVisibility() {
        // TODO Spinner
        if(m_searchModel.getCurrentQueryResults() != null && m_searchModel.getTotalCurrentQueryResults() > 0)
        {
            m_view.setVisibility(View.VISIBLE);
            return;
        }
        m_view.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Pair<Integer, Integer> providerIndex = m_adapter.getProviderIndex(position);
        SearchProviderQueryResult result = m_searchModel.getCurrentQueryResults().get(providerIndex.first);
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
