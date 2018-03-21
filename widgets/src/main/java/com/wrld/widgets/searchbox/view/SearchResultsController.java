package com.wrld.widgets.searchbox.view;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuOption;
import com.wrld.widgets.searchbox.model.SearchResultSelectedListener;
import com.wrld.widgets.searchbox.model.SearchResultsListener;
import com.wrld.widgets.searchbox.model.SearchResult;
import com.wrld.widgets.searchbox.model.ObservableSearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchQueryModel;
import com.wrld.widgets.searchbox.model.SearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;

import java.util.List;
import java.util.Locale;

public class SearchResultsController implements SearchResultsListener,
        AdapterView.OnItemClickListener,
        View.OnFocusChangeListener, MenuViewListener {

    private final SearchQueryModel m_model;
    private final SearchResultsModel m_searchResultsModel;
    private View m_noResultsViewContainer;
    private SearchView m_searchView;
    private SearchViewFocusObserver m_searchViewFocusObserver;
    private final View m_viewRoot;
    private final ListView m_listView;
    private final SearchResultsAdapter m_adapter;
    private SearchResultsViewObserver m_viewObserver;
    private MenuViewObserver m_menuViewObserver;

    private boolean m_resultsHidden;
    private boolean m_menuOpened;

    public SearchResultsController(SearchQueryModel searchModel,
                                   SearchResultsModel searchResultsModel,
                                   View viewRoot,
                                   SearchView searchView,
                                   SearchViewFocusObserver searchViewFocusObserver,
                                   View noResultsViewContainer,
                                   SearchResultsViewObserver viewObserver,
                                   MenuViewObserver menuViewObserver)
    {
        m_model = searchModel;
        m_searchResultsModel = searchResultsModel;
        m_viewObserver = viewObserver;
        m_noResultsViewContainer = noResultsViewContainer;

        m_searchResultsModel.addResultListener(this);

        // TODO Pass the 3 in.
        m_adapter = new SearchResultsAdapter(m_searchResultsModel, m_model, LayoutInflater.from(viewRoot.getContext()), 3);

        m_viewRoot = viewRoot;
        m_listView = (ListView)m_viewRoot.findViewById(R.id.searchbox_search_results_list);
        m_listView.setOnItemClickListener(this);
        m_listView.setAdapter(m_adapter);

        m_resultsHidden = false;
        m_menuOpened = false;

        m_searchView = searchView;
        m_searchViewFocusObserver = searchViewFocusObserver;
        m_searchViewFocusObserver.addListener(this);

        m_menuViewObserver = menuViewObserver;
        m_menuViewObserver.addMenuListener(this);

        updateVisibility();
    }

    public void clean() {
        m_menuViewObserver.removeMenuListener(this);
        m_searchViewFocusObserver.removeListener(this);
        m_searchResultsModel.removeResultListener(this);
    }

    @Override
    public void onSearchResultsRecieved(SearchQuery query, List<SearchProviderQueryResult> results) {
        if(m_resultsHidden) {
            minimizeResults();
        }
        updateVisibility();
        m_adapter.refresh(true);
    }

    @Override
    public void onSearchResultsCleared() {
        maximizeResults();
        updateVisibility();
        m_adapter.refresh(false);
    }

    @Override
    public void onSearchResultsSelected(SearchResult result) {
        minimizeResults();
    }

    private void updateVisibility() {
        boolean hasSearchResults = m_searchResultsModel.getTotalCurrentQueryResults() > 0;
        if(hasSearchResults && !m_resultsHidden && !m_menuOpened) {
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
                m_searchResultsModel.selectSearchResult(searchResult);
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(m_resultsHidden && hasFocus && m_model.getCurrentQuery() != null) {
            maximizeResults();
        }
    }

    public void minimizeResults() {
        if(!m_resultsHidden) {
            m_resultsHidden = true;
            m_viewObserver.onSearchResultsHidden();
        }

        if(m_model.getCurrentQuery() != null) {
            String hiddenResultsQueryString = String.format(Locale.getDefault(), "%s  (%d)",
                    m_model.getCurrentQuery().getQueryString(),
                    m_searchResultsModel.getTotalCurrentQueryResults());
            m_searchView.clearFocus();
            m_searchView.setQuery(hiddenResultsQueryString, false);
            updateVisibility();
        }
    }

    public void maximizeResults() {
        if(m_resultsHidden) {
            m_resultsHidden = false;
            m_viewObserver.onSearchResultsShown();
        }

        if(m_model.getCurrentQuery() != null) {
            String originalQueryString = m_model.getCurrentQuery().getQueryString();
            m_searchView.setQuery(originalQueryString, false);
            updateVisibility();
        }
    }

    @Override
    public void onOpened() {
        m_menuOpened = true;
        updateVisibility();
    }

    @Override
    public void onClosed() {
        m_menuOpened = false;
        updateVisibility();
    }

    @Override
    public void onOptionExpanded(MenuOption option) {

    }

    @Override
    public void onOptionCollapsed(MenuOption option) {

    }

    @Override
    public void onOptionSelected(MenuOption option) {

    }

    @Override
    public void onChildSelected(MenuChild option) {

    }
}
