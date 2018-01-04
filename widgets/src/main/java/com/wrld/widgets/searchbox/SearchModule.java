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

public class SearchModule implements SearchModuleFacade, SearchQueryHandler {

    private SearchModuleController m_searchModuleController;

    private SearchProvider[] m_searchProviders;
    private SuggestionProvider[] m_suggestionProviders;

    private ArrayList<SearchResultSet> m_searchProviderSets;
    private ArrayList<SearchResultSet> m_suggestionProviderSets;

    private SearchResultSetFactory m_searchResultSetFactory;

    private SearchMenuContent m_menuContent;

    private TextView m_queryDisplay;

    private ArrayList<QueryPerformedCallback> m_queryPerformedCallbacks;
    private ArrayList<QueryPerformedCallback> m_suggestionPerformedHandlers;
    private ArrayList<QueryCompletedCallback> m_queryCompletedCallbacks;
    private ArrayList<MenuVisibilityChangedCallback> m_menuVisibilityChangedCallbacks;
    private ArrayList<SearchResultSelectedCallback> m_searchResultSelectedCallbacks;

    public SearchModule(ViewGroup appSearchAreaView) {
        initialiseMembers();
        inflateViewsAndAssignControllers(appSearchAreaView);
    }

    private void initialiseMembers(){
        m_searchProviders                   = new SearchProvider[0];
        m_suggestionProviders               = new SuggestionProvider[0];

        m_searchProviderSets                = new ArrayList<SearchResultSet>();
        m_suggestionProviderSets            = new ArrayList<SearchResultSet>();

        m_queryPerformedCallbacks = new ArrayList<QueryPerformedCallback> ();
        m_suggestionPerformedHandlers       = new ArrayList<QueryPerformedCallback> ();
        m_queryCompletedCallbacks = new ArrayList<QueryCompletedCallback> ();
        m_menuVisibilityChangedCallbacks = new ArrayList<MenuVisibilityChangedCallback> ();
        m_searchResultSelectedCallbacks = new ArrayList<SearchResultSelectedCallback> ();
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

        SearchMenuController searchMenuController = new SearchMenuController(
                (ViewStub) root.findViewById(R.id.searchbox_menu_container_stub),
                m_searchModuleController,
                m_menuContent);
        m_searchModuleController.setSearchMenuController(searchMenuController);

        setDefaultSearchResultViewFactory(new DefaultSearchResultViewFactory(R.layout.search_result));

        int matchedTextColor = ContextCompat.getColor(context, R.color.searchbox_autcomplete_list_header_font_matched);

        DefaultSuggestionViewFactory suggestionViewFactory = new DefaultSuggestionViewFactory(
                R.layout.search_suggestion,
                this,
                matchedTextColor);

        setDefaultSuggestionViewFactory(suggestionViewFactory);

        m_searchModuleController.showQueryBox(searchController);
        m_searchModuleController.hideResults(searchController);

        searchMenuController.setDefaultMenuContent(root.getContext());
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

        m_searchModuleController.removeAllSearchProviders();

        for(SearchProvider searchProvider : searchProviders) {
            SearchResultSet searchResultSet = m_searchResultSetFactory.createResultSetForSearchProvider(searchProvider);
            SearchResultsController searchResultsController = m_searchModuleController.addSearchProvider(searchProvider, searchResultSet);
            searchResultSet.addOnResultChangedHandler(searchResultsController.getUpdateCallback());
            m_searchProviderSets.add(searchResultSet);
        }

        m_searchProviders = searchProviders;
    }

    @Override
    public void setSuggestionProviders(SuggestionProvider[] suggestionProviders){
        for(SearchResultSet set : m_suggestionProviderSets){
            set.deregisterWithProvider();
        }
        m_suggestionProviderSets.clear();

        m_searchModuleController.removeAllSuggestionProviders();

        for(SuggestionProvider suggestionProvider : suggestionProviders) {
            SearchResultSet searchResultSet = m_searchResultSetFactory.createResultSetForSuggestionProvider(suggestionProvider);
            SearchResultsController searchResultsController = m_searchModuleController.addSuggestionProvider(suggestionProvider, searchResultSet);
            searchResultSet.addOnResultChangedHandler(searchResultsController.getUpdateCallback());
            m_suggestionProviderSets.add(searchResultSet);
        }

        m_suggestionProviders = suggestionProviders;
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
    public void addSuggestionPerformedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_suggestionPerformedHandlers.add(queryPerformedCallback);
    }

    @Override
    public void removeSuggestionPerformedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_suggestionPerformedHandlers.remove(queryPerformedCallback);
    }

    @Override
    public void addSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_queryCompletedCallbacks.add(queryCompletedCallback);
    }

    @Override
    public void removeSearchCompletedCallback(QueryCompletedCallback queryCompletedCallback) {
        m_queryCompletedCallbacks.remove(queryCompletedCallback);
    }

    @Override
    public void addMenuVisibilityCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback) {
        m_menuVisibilityChangedCallbacks.add(menuVisibilityChangedCallback);
    }

    @Override
    public void removeMenuVisibilityChangedCallback(MenuVisibilityChangedCallback menuVisibilityChangedCallback) {
        m_menuVisibilityChangedCallbacks.remove(menuVisibilityChangedCallback);
    }

    @Override
    public void addSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback) {
        m_searchResultSelectedCallbacks.add(searchResultSelectedCallback);
    }

    @Override
    public void removeSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback) {
        m_searchResultSelectedCallbacks.remove(searchResultSelectedCallback);
    }

    //endregion

    @Override
    public void searchFor(String query){
        for(SearchProvider provider : m_searchProviders){
            provider.getSearchResults(query);
        }
        for(QueryPerformedCallback queryPerformedCallback : m_queryPerformedCallbacks){
            queryPerformedCallback.onQuery(query);
        }
    }

    @Override
    public void getSuggestionsFor(String text){
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
