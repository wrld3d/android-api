package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.wrld.widgets.R;

import java.util.ArrayList;

public class SearchModule implements SearchModuleFacade, SearchQueryHandler {

    private SearchModuleController m_searchModuleController;

    private SearchProvider[] m_searchProviders;
    private SuggestionProvider[] m_suggestionProviders;

    private ArrayList<SearchResultSet> m_searchProviderSets;
    private ArrayList<SearchResultSet> m_suggestionProviderSets;

    private SearchResultSetFactory m_searchResultSetFactory;

    private SearchMenuContent m_menuContent;

    public SearchModule(ViewGroup appSearchAreaView) {
        LayoutInflater inflater = LayoutInflater.from(appSearchAreaView.getContext());
        View root = inflater.inflate(R.layout.search_layout, appSearchAreaView, true);

        m_menuContent = new SearchMenuContent(inflater);

        m_searchModuleController = new SearchModuleController();

        m_searchResultSetFactory = new SearchResultSetFactory();

        m_searchModuleController.setSearchQueryHandler(this);

        ViewGroup searchContainer = (ViewGroup) root.findViewById(R.id.searchbox_search_container);
        inflater.inflate(R.layout.searchbox_query, searchContainer);
        SearchController searchController = new SearchController(searchContainer, m_searchModuleController);
        m_searchModuleController.setQueryBoxController(searchController);

        ViewGroup resultSpace = (ViewGroup) root.findViewById(R.id.searchbox_results_space);
        SearchResultScreenController resultSetController = new SearchResultScreenController(
                resultSpace,
                m_searchModuleController);
        m_searchModuleController.setSearchResultsSetController(resultSetController);

        SearchMenuController searchMenuController = new SearchMenuController(
                (ViewStub) root.findViewById(R.id.searchbox_menu_container_stub),
                m_searchModuleController,
                m_menuContent);
        m_searchModuleController.setSearchMenuController(searchMenuController);

        setDefaultSearchResultViewFactory(new DefaultSearchResultViewFactory(R.layout.search_result));
        setDefaultSuggestionViewFactory(new DefaultSuggestionViewFactory(R.layout.search_suggestion));

        m_searchModuleController.showQueryBox(searchController);
        m_searchModuleController.hideResults(searchController);

        m_searchProviders = new SearchProvider[0];
        m_suggestionProviders = new SuggestionProvider[0];
        m_searchProviderSets = new ArrayList<SearchResultSet>();
        m_suggestionProviderSets = new ArrayList<SearchResultSet>();

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
    public SearchBoxMenuGroup addGroup(String title) {
        return m_menuContent.addGroup(title);
    }

    //TODO public void addConsumer(SearchResultConsumer consumer) { }

    @Override
    public void searchFor(String query){
        for(SearchProvider provider : m_searchProviders){
            provider.getSearchResults(query);
        }
    }

    @Override
    public void getSuggestionsFor(String text){
        for(SuggestionProvider provider : m_suggestionProviders){
            provider.getSuggestions(text);
        }
    }
}
