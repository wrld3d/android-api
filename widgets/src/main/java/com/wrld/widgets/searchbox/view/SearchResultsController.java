package com.wrld.widgets.searchbox.view;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.SearchResultsListener;
import com.wrld.widgets.searchbox.model.SearchResult;
import com.wrld.widgets.searchbox.model.ObservableSearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchQueryModel;

import java.util.List;
import java.util.Locale;

public class SearchResultsController implements SearchResultsListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener {

    private final SearchQueryModel m_model;
    private final ObservableSearchResultsModel m_searchResultsModel;
    private View m_noResultsViewContainer;
    private SearchView m_searchView;
    private SearchViewFocusObserver m_searchViewFocusObserver;
    private final View m_viewRoot;
    private final ListView m_listView;
    private final SearchResultsAdapter m_adapter;
    private boolean m_resultsHidden;

    public SearchResultsController(SearchQueryModel searchModel,
                                   ObservableSearchResultsModel searchResultsModel,
                                   View viewRoot,
                                   SearchView searchView,
                                   SearchViewFocusObserver searchViewFocusObserver,
                                   View noResultsViewContainer)
    {
        m_model = searchModel;
        m_searchResultsModel = searchResultsModel;
        m_noResultsViewContainer = noResultsViewContainer;

        m_searchResultsModel.addResultListener(this);

        // TODO Pass the 3 in.
        m_adapter = new SearchResultsAdapter(m_searchResultsModel, m_model, LayoutInflater.from(viewRoot.getContext()), 3);

        m_viewRoot = viewRoot;
        m_listView = (ListView)m_viewRoot.findViewById(R.id.searchbox_search_results_list);
        m_listView.setOnItemClickListener(this);
        m_listView.setAdapter(m_adapter);

        m_resultsHidden = false;

        m_searchView = searchView;
        m_searchViewFocusObserver = searchViewFocusObserver;
        m_searchViewFocusObserver.addListener(this);

        updateVisibility();
    }

    public void clean() {
        m_searchViewFocusObserver.removeListener(this);
        m_searchResultsModel.removeResultListener(this);
    }

    @Override
    public void onSearchResultsRecieved(SearchQuery query, List<SearchProviderQueryResult> results) {
        m_resultsHidden = false;
        updateVisibility();
        m_adapter.refresh(true);
    }

    @Override
    public void onSearchResultsCleared() {
        updateVisibility();
        m_adapter.refresh(false);
    }

    private void updateVisibility() {
        boolean hasSearchResults = m_searchResultsModel.getTotalCurrentQueryResults() > 0;
        if(hasSearchResults && !m_resultsHidden) {
            m_viewRoot.setVisibility(View.VISIBLE);
            m_noResultsViewContainer.setVisibility(View.GONE);
        }
        else {
            m_viewRoot.setVisibility(View.GONE);
        }

        boolean showingNoResults = !m_resultsHidden &&
                m_model.getCurrentQuery() != null &&
                m_model.getCurrentQuery().isComplete() &&
                m_searchResultsModel.getTotalCurrentQueryResults() == 0;
        m_noResultsViewContainer.setVisibility(showingNoResults ? View.VISIBLE : View.GONE);

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
                SearchResult searchResult = result.getResults()[providerIndex.second];
                selectSearchResult(searchResult);
            }
        }
    }

    private void selectSearchResult(SearchResult searchResult) {
        searchResult.select();
        m_resultsHidden = true;
        String hiddenResultsQueryString = String.format(Locale.getDefault(), "%s  (%d)",
                m_model.getCurrentQuery().getQueryString(),
                m_searchResultsModel.getTotalCurrentQueryResults());
        m_searchView.clearFocus();
        m_searchView.setQuery(hiddenResultsQueryString, false);
        updateVisibility();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(m_resultsHidden && hasFocus && m_model.getCurrentQuery() != null) {

            m_resultsHidden = false;
            String originalQueryString = m_model.getCurrentQuery().getQueryString();
            m_searchView.setQuery(originalQueryString, false);
            updateVisibility();
        }
    }
}
