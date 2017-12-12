package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrld.widgets.R;

import java.util.ArrayList;

public class SearchModule implements SearchModuleFacade, SearchQueryHandler {

    private SearchModuleController m_searchModuleController;

    private SearchProvider[] m_searchProviders;
    private SuggestionProvider[] m_suggestionProviders;

    private ArrayList<SearchResultSet> m_searchProviderSets;
    private ArrayList<SearchResultSet> m_suggestionProviderSets;

    private SearchResultSetFactory m_searchResultSetFactory;

    public SearchModule(ViewGroup appSearchAreaView) {
        LayoutInflater inflater = LayoutInflater.from(appSearchAreaView.getContext());
        View root = inflater.inflate(R.layout.search_layout, appSearchAreaView, true);

        m_searchModuleController = new SearchModuleController();

        m_searchResultSetFactory = new SearchResultSetFactory();

        m_searchModuleController.setSearchQueryHandler(this);

        ViewGroup searchContainer = (ViewGroup) root.findViewById(R.id.searchbox_search_container);
        inflater.inflate(R.layout.searchbox_search, searchContainer);
        SearchController searchController = new SearchController(searchContainer, m_searchModuleController);
        m_searchModuleController.setQueryBoxController(searchController);

        ViewGroup resultSpace = (ViewGroup) root.findViewById(R.id.searchbox_results_space);
        SearchResultScreenController resultSetController = new SearchResultScreenController(resultSpace, m_searchModuleController);
        m_searchModuleController.setSearchResultsSetController(resultSetController);

        setDefaultSearchResultViewFactory(new DefaultSearchResultViewFactory(R.layout.search_result));
        setDefaultSuggestionViewFactory(new DefaultSuggestionViewFactory(R.layout.search_suggestion));

        m_searchModuleController.showQueryBox(searchController);
        m_searchModuleController.hideResults(searchController);

        m_searchProviders = new SearchProvider[0];
        m_suggestionProviders = new SuggestionProvider[0];
        m_searchProviderSets = new ArrayList<SearchResultSet>();
        m_suggestionProviderSets = new ArrayList<SearchResultSet>();
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
        m_searchModuleController.setSearchProviders(searchProviders, m_searchResultSetFactory);

        m_searchProviders = searchProviders;
    }

    @Override
    public void setSuggestionProviders(SuggestionProvider[] suggestionProviders){
        for(SearchResultSet set : m_suggestionProviderSets){
            set.deregisterWithProvider();
        }

        m_suggestionProviderSets.clear();
        m_searchModuleController.setSuggestionProviders(suggestionProviders, m_searchResultSetFactory);

        m_suggestionProviders = suggestionProviders;
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
