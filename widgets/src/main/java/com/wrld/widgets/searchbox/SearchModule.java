package com.wrld.widgets.searchbox;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.api.events.MenuVisibilityChangedCallback;
import com.wrld.widgets.searchbox.api.events.QueryCompletedCallback;
import com.wrld.widgets.searchbox.api.events.QueryPerformedCallback;
import com.wrld.widgets.searchbox.api.events.SearchResultSelectedCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchModule implements SearchModuleFacade, SearchQueryHandler {

    private SearchModuleController m_searchModuleController;
    private SearchMenuController m_searchMenuController;

    private SearchProvider[] m_searchProviders;
    private SuggestionProvider[] m_suggestionProviders;

    private ArrayList<SearchResultSet> m_searchProviderSets;
    private ArrayList<SearchResultSet> m_suggestionProviderSets;

    private SearchResultSetFactory m_searchResultSetFactory;

    private SearchMenuContent m_menuContent;

    private TextView m_queryDisplay;

    private ArrayList<QueryPerformedCallback> m_queryPerformedCallbacks;
    private ArrayList<QueryPerformedCallback> m_suggestionPerformedHandlers;
    private ArrayList<QueryCompletedCallback> m_searchCompletedCallbacks;
    private ArrayList<QueryCompletedCallback> m_suggestionsReturnedCallbacks;

    private int m_responsesToLastRequest;
    private ArrayList<SearchResult> m_resultsFromLastRequest;

    private QueryCompletedCallback m_onSearchResultsReturnedCallback;
    private QueryCompletedCallback m_onSuggestionsReturnedCallback;

    public SearchModule(ViewGroup appSearchAreaView) {
        initialiseMembers();
        inflateViewsAndAssignControllers(appSearchAreaView);

        m_onSearchResultsReturnedCallback = onSearchResultsReturned();
        m_onSuggestionsReturnedCallback = onSuggestionsReturned();
    }

    private void initialiseMembers(){
        m_searchProviders                   = new SearchProvider[0];
        m_suggestionProviders               = new SuggestionProvider[0];

        m_searchProviderSets                = new ArrayList<SearchResultSet>();
        m_suggestionProviderSets            = new ArrayList<SearchResultSet>();

        m_queryPerformedCallbacks           = new ArrayList<QueryPerformedCallback> ();
        m_suggestionPerformedHandlers       = new ArrayList<QueryPerformedCallback> ();

        m_searchCompletedCallbacks          = new ArrayList<QueryCompletedCallback> ();
        m_suggestionsReturnedCallbacks      = new ArrayList<QueryCompletedCallback> ();

        m_resultsFromLastRequest            = new ArrayList<SearchResult>();
    }

    private void inflateViewsAndAssignControllers(ViewGroup container) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        m_menuContent                       = new SearchMenuContent(inflater);
        m_searchModuleController            = new SearchModuleController();
        m_searchResultSetFactory            = new SearchResultSetFactory();

        View root = inflater.inflate(R.layout.search_layout, container, true);

        m_searchModuleController.setSearchQueryHandler(this);

        ViewGroup searchContainer = (ViewGroup) root.findViewById(R.id.searchbox_search_container);
        inflater.inflate(R.layout.searchbox_query, searchContainer);
        SearchController searchController = new SearchController(searchContainer, m_searchModuleController);
        m_searchModuleController.setQueryBoxController(searchController);
        m_queryDisplay = (TextView) searchContainer.findViewById(R.id.searchbox_search_querybox);

        SearchResultScreenController resultSetController = new SearchResultScreenController(
                root,
                m_searchModuleController);
        m_searchModuleController.setSearchResultsSetController(resultSetController);

        m_searchMenuController = new SearchMenuController(
                (ViewStub) root.findViewById(R.id.searchbox_menu_container_stub),
                m_searchModuleController,
                m_menuContent);
        m_searchModuleController.setSearchMenuController(m_searchMenuController);

        setDefaultSearchResultViewFactory(new DefaultSearchResultViewFactory(R.layout.search_result));

        int matchedTextColor = ContextCompat.getColor(context, R.color.searchbox_autcomplete_list_header_font_matched);

        DefaultSuggestionViewFactory suggestionViewFactory = new DefaultSuggestionViewFactory(
                R.layout.search_suggestion,
                this,
                matchedTextColor);

        setDefaultSuggestionViewFactory(suggestionViewFactory);

        m_searchModuleController.showQueryBox(searchController);
        m_searchModuleController.hideResults(searchController);

        m_searchMenuController.setDefaultMenuContent(root.getContext());
    }

    @Override
    public void setDefaultSearchResultViewFactory(SearchResultViewFactory factory) {
        //TODO
    }

    @Override
    public void setDefaultSuggestionViewFactory(SearchResultViewFactory factory) {
        //TODO
    }

    @Override
    public void setSearchProviders(SearchProvider[] searchProviders) {

        for(SearchResultSet set : m_searchProviderSets){
            set.deregisterWithProvider();
        }
        m_searchProviderSets.clear();
        for(SearchProvider searchProvider : m_searchProviders) {
            searchProvider.removeSearchCompletedCallback(m_onSearchResultsReturnedCallback);
        }

        m_searchModuleController.removeAllSearchProviders();

        for(SearchProvider searchProvider : searchProviders) {
            SearchResultSet searchResultSet = m_searchResultSetFactory.createResultSetForSearchProvider(searchProvider);
            SearchResultsController searchResultsController = m_searchModuleController.addSearchProvider(searchProvider, searchResultSet);
            searchResultSet.addOnResultChangedHandler(searchResultsController.getUpdateCallback());
            m_searchProviderSets.add(searchResultSet);
            searchProvider.addSearchCompletedCallback(m_onSearchResultsReturnedCallback);
        }

        m_searchProviders = searchProviders;
    }

    @Override
    public void setSuggestionProviders(SuggestionProvider[] suggestionProviders){
        for(SearchResultSet set : m_suggestionProviderSets){
            set.deregisterWithProvider();
        }
        m_suggestionProviderSets.clear();
        for(SuggestionProvider suggestionProvider : m_suggestionProviders) {
            suggestionProvider.removeSuggestionsReceivedCallback(m_onSuggestionsReturnedCallback);
        }

        m_searchModuleController.removeAllSuggestionProviders();

        for(SuggestionProvider suggestionProvider : suggestionProviders) {
            SearchResultSet searchResultSet = m_searchResultSetFactory.createResultSetForSuggestionProvider(suggestionProvider);
            SearchResultsController searchResultsController = m_searchModuleController.addSuggestionProvider(suggestionProvider, searchResultSet);
            searchResultSet.addOnResultChangedHandler(searchResultsController.getUpdateCallback());
            m_suggestionProviderSets.add(searchResultSet);
            suggestionProvider.addSuggestionsReceivedCallback(m_onSuggestionsReturnedCallback);
        }

        m_suggestionProviders = suggestionProviders;
    }


    private QueryCompletedCallback onSearchResultsReturned(){
        return new QueryCompletedCallback() {
            @Override
            public void onQueryCompleted(final SearchResult[] returnedResults) {
                m_resultsFromLastRequest.addAll(Arrays.asList(returnedResults));
                ++m_responsesToLastRequest;
                checkAllSearchProvidersReturned();
            }
        };
    }

    private QueryCompletedCallback onSuggestionsReturned(){
        return new QueryCompletedCallback() {
            @Override
            public void onQueryCompleted(final SearchResult[] returnedResults) {
                m_resultsFromLastRequest.addAll(Arrays.asList(returnedResults));
                ++m_responsesToLastRequest;
                checkAllSuggestionsReturned();
            }
        };
    }

    private void checkAllSearchProvidersReturned(){
        if(m_responsesToLastRequest == m_searchProviders.length){
            SearchResult[] allReturnedResults = new SearchResult[m_resultsFromLastRequest.size()];
            for(QueryCompletedCallback callback : m_searchCompletedCallbacks){
                callback.onQueryCompleted(m_resultsFromLastRequest.toArray(allReturnedResults));
            }
        }
    }

    private void checkAllSuggestionsReturned(){
        if(m_responsesToLastRequest == m_suggestionProviders.length){
            for(QueryCompletedCallback callback : m_suggestionsReturnedCallbacks){
                SearchResult[] allReturnedResults = new SearchResult[m_resultsFromLastRequest.size()];
                callback.onQueryCompleted(m_resultsFromLastRequest.toArray(allReturnedResults));
            }
        }
    }

    @Override
    public SearchBoxMenuGroup getGroupByIndex(int index) {
        return m_menuContent.getSearchBoxMenuGroup(index);
    }

    @Override
    public void showDefaultView() {
        m_searchModuleController.showDefaultView();
    }

    @Override
    public SearchBoxMenuGroup addGroup(String title) {
        return m_menuContent.addGroup(title);
    }

    //region EVENT REGISTRATION

    @Override
    public void addSearchPerformedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_queryPerformedCallbacks.add(queryPerformedCallback);
    }

    @Override
    public void removeSearchPerformedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_queryPerformedCallbacks.remove(queryPerformedCallback);
    }

    @Override
    public void addSuggestionsRequestedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_suggestionPerformedHandlers.add(queryPerformedCallback);
    }

    @Override
    public void removeSuggestionsRequestedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_suggestionPerformedHandlers.remove(queryPerformedCallback);
    }

    @Override
    public void addSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_searchCompletedCallbacks.add(queryCompletedCallback);
    }

    @Override
    public void removeSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_searchCompletedCallbacks.remove(queryCompletedCallback);
    }

    @Override
    public void addSuggestionsReturnedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_suggestionsReturnedCallbacks.add(queryCompletedCallback);
    }

    @Override
    public void removeSuggestionsReturnedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_suggestionsReturnedCallbacks.remove(queryCompletedCallback);
    }

    @Override
    public void addMenuVisibilityCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback) {
        m_searchMenuController.addMenuVisibilityCallback(menuVisibilityChangedCallback);
    }

    @Override
    public void removeMenuVisibilityChangedCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback) {
        m_searchMenuController.removeMenuVisibilityChangedCallback(menuVisibilityChangedCallback);
    }

    @Override
    public void addSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback) {
        m_searchModuleController.addSearchResultSelectedCallback(searchResultSelectedCallback);
    }

    @Override
    public void removeSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback) {
        m_searchModuleController.removeSearchResultSelectedCallback(searchResultSelectedCallback);
    }

    @Override
    public void addSuggestionSelectedCallback(SearchResultSelectedCallback suggestionSelectedCallback) {
        m_searchModuleController.addSuggestionSelectedCallback(suggestionSelectedCallback);
    }

    @Override
    public void removeSuggestionSelectedCallback(SearchResultSelectedCallback suggestionSelectedCallback) {
        m_searchModuleController.removeSuggestionSelectedCallback(suggestionSelectedCallback);
    }

    //endregion

    private void clearRequests(){
        for(SearchProvider provider : m_searchProviders){
            if(provider.hasActiveRequest()){
                provider.cancelActiveRequest();
            }
        }
        for(SuggestionProvider provider : m_suggestionProviders) {
            if (provider.hasActiveRequest()) {
                provider.cancelActiveRequest();
            }
        }
        m_responsesToLastRequest = 0;
        m_resultsFromLastRequest.clear();
    }

    @Override
    public void searchFor(String query){
        clearRequests();
        for(SearchProvider provider : m_searchProviders){
            provider.getSearchResults(query);
        }
        for(QueryPerformedCallback queryPerformedCallback : m_queryPerformedCallbacks){
            queryPerformedCallback.onQuery(query);
        }
    }

    @Override
    public void getSuggestionsFor(String text){
        clearRequests();
        for(SuggestionProvider provider : m_suggestionProviders){
            provider.getSuggestions(text);
        }
        for(QueryPerformedCallback suggestionPerformedHandler : m_suggestionPerformedHandlers){
            suggestionPerformedHandler.onQuery(text);
        }
    }

    @Override
    public CharSequence getCurrentQuery(){
        return m_queryDisplay.getText();
    }
}
