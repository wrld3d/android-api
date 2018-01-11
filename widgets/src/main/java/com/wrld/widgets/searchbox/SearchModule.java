package com.wrld.widgets.searchbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.api.DefaultSearchResultViewFactory;
import com.wrld.widgets.searchbox.api.DefaultSuggestionViewFactory;
import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SearchQueryHandler;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.searchbox.api.events.MenuVisibilityChangedCallback;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;
import com.wrld.widgets.searchbox.api.events.QueryPerformedCallback;
import com.wrld.widgets.searchbox.api.events.SearchResultSelectedCallback;
import com.wrld.widgets.searchbox.menu.SearchBoxMenuGroup;
import com.wrld.widgets.ui.TextHighlighter;

import java.util.ArrayList;

class SearchModule extends RelativeLayout implements com.wrld.widgets.searchbox.api.SearchModule, SearchQueryHandler {

    private SearchModuleController m_searchModuleController;
    private SearchMenuController m_searchMenuController;

    private SearchProvider[] m_searchProviders;
    private SuggestionProvider[] m_suggestionProviders;

    private ArrayList<SearchResultSet> m_searchProviderSets;
    private ArrayList<SearchResultSet> m_suggestionProviderSets;

    private SearchResultSetFactory m_searchResultSetFactory;

    private SearchMenuContent m_menuContent;

    private SearchView m_queryDisplay;

    private ArrayList<QueryPerformedCallback> m_queryPerformedCallbacks;
    private ArrayList<QueryPerformedCallback> m_suggestionPerformedCallbacks;

    private Query m_currentQuery;

    private CurrentQueryObserver m_currentQueryObserver;

    private SetCollection m_searchResultSetCollection;
    private SetCollection m_suggestionSetCollection;

    private int m_numSuggestions;
    private int m_maxResults;

    public SearchModule(Context context) {
        super(context);

        initialise();

        m_numSuggestions = 3;
    }

    public SearchModule(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        initialise();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.SearchModule,
                0, 0);

        m_numSuggestions = a.getInteger(R.styleable.SearchModule_maxSuggestions, 3);
        m_maxResults  = a.getInteger(R.styleable.SearchModule_maxResults, 3);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        inflateViewsAndAssignControllers();
    }

    private void initialise(){
        m_searchProviders                   = new SearchProvider[0];
        m_suggestionProviders               = new SuggestionProvider[0];

        m_searchProviderSets                = new ArrayList<SearchResultSet>();
        m_suggestionProviderSets            = new ArrayList<SearchResultSet>();

        m_queryPerformedCallbacks           = new ArrayList<QueryPerformedCallback> ();
        m_suggestionPerformedCallbacks      = new ArrayList<QueryPerformedCallback> ();

        m_currentQueryObserver = new CurrentQueryObserver();
    }


    public void inflateViewsAndAssignControllers() {
        Context context = this.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        m_menuContent                       = new SearchMenuContent(inflater);
        m_searchModuleController            = new SearchModuleController();
        m_searchResultSetFactory            = new SearchResultSetFactory(m_numSuggestions, m_maxResults);

        View root = inflater.inflate(R.layout.search_layout, this, true);

        m_searchModuleController.setSearchQueryHandler(this);

        ViewGroup searchContainer = (ViewGroup) root.findViewById(R.id.searchbox_search_container);
        SearchController searchController = new SearchController(searchContainer, m_searchModuleController);
        m_searchModuleController.setQueryBoxController(searchController);
        m_queryDisplay = (SearchView) searchContainer.findViewById(R.id.searchbox_search_searchview);

        m_suggestionSetCollection = new SetCollection();
        m_searchResultSetCollection = new SetCollection();

        SearchResultScreenController resultSetController = new SearchResultScreenController(
                root,
                m_searchModuleController,
                m_searchResultSetCollection,
                m_suggestionSetCollection,
                m_currentQueryObserver);

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
                new TextHighlighter(matchedTextColor));

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
        if(m_currentQuery != null) {
            m_currentQuery.cancel();
        }
        for(SearchResultSet set : m_searchProviderSets){
            set.deregisterWithProvider();
        }
        m_searchProviderSets.clear();

        ArrayList<SearchResultViewFactory> searchResultViewFactories = new ArrayList<SearchResultViewFactory>();
        for(SearchProvider searchProvider : searchProviders) {
            SearchResultSet searchResultSet = m_searchResultSetFactory.createResultSetForSearchProvider(searchProvider);
            m_searchProviderSets.add(searchResultSet);
            searchResultViewFactories.add(searchProvider.getResultViewFactory());
        }

        m_searchResultSetCollection.setSets(m_searchProviderSets);

        m_searchModuleController.setSearchResultViewFactories(searchResultViewFactories);

        m_currentQueryObserver.registerWithSearchProviders(searchProviders);
        m_searchProviders = searchProviders;
    }

    @Override
    public void setSuggestionProviders(SuggestionProvider[] suggestionProviders){

        if(m_currentQuery != null) {
            m_currentQuery.cancel();
        }

        for(SearchResultSet set : m_suggestionProviderSets){
            set.deregisterWithProvider();
        }

        m_suggestionProviderSets.clear();

        ArrayList<SearchResultViewFactory> suggestionViewFactories = new ArrayList<SearchResultViewFactory>();
        for(SuggestionProvider suggestionProvider : suggestionProviders) {
            SearchResultSet searchResultSet = m_searchResultSetFactory.createResultSetForSuggestionProvider(suggestionProvider);
            m_suggestionProviderSets.add(searchResultSet);
            suggestionViewFactories.add(suggestionProvider.getSuggestionViewFactory());
        }

        m_searchModuleController.setSuggestionViewFactories(suggestionViewFactories);

        m_suggestionSetCollection.setSets(m_suggestionProviderSets);

        m_currentQueryObserver.registerWithSuggestionProviders(suggestionProviders);

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
    public void addSuggestionsRequestedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_suggestionPerformedCallbacks.add(queryPerformedCallback);
    }

    @Override
    public void removeSuggestionsRequestedCallback(QueryPerformedCallback queryPerformedCallback) {
        m_suggestionPerformedCallbacks.remove(queryPerformedCallback);
    }

    @Override
    public void addSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_currentQueryObserver.addSearchCompletedCallback(queryResultsReadyCallback);
    }

    @Override
    public void removeSearchCompletedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_currentQueryObserver.removeSearchCompletedCallback(queryResultsReadyCallback);
    }

    @Override
    public void addSuggestionsReturnedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_currentQueryObserver.addSuggestionsReturnedCallback(queryResultsReadyCallback);
    }

    @Override
    public void removeSuggestionsReturnedCallback(QueryResultsReadyCallback queryResultsReadyCallback) {
        m_currentQueryObserver.removeSuggestionsReturnedCallback(queryResultsReadyCallback);
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

    @Override
    public void doSearch(String search) {
        m_searchModuleController.doSearch(m_searchMenuController, search);
    }

    @Override
    public void searchFor(String queryText){

        if(m_currentQuery != null && !m_currentQuery.hasCompleted()){
            m_currentQuery.cancel();
        }

        m_currentQuery = new Query(queryText, m_currentQueryObserver.onAllResultsReturned(), m_searchProviders.length);
        m_currentQueryObserver.setCurrentQuery(m_currentQuery);

        for(SearchProvider provider : m_searchProviders){
            provider.getSearchResults(m_currentQuery);
        }

        QueryPerformedCallback[] callbacks = new QueryPerformedCallback[m_queryPerformedCallbacks.size()];
        m_queryPerformedCallbacks.toArray(callbacks);
        for(QueryPerformedCallback queryPerformedCallback : callbacks){
            queryPerformedCallback.onQuery(queryText);
        }
    }

    @Override
    public void getSuggestionsFor(String text){

        if(m_currentQuery != null && !m_currentQuery.hasCompleted()){
            m_currentQuery.cancel();
        }

        m_currentQuery = new Query(text, m_currentQueryObserver.onAllSuggestionsReturned(), m_suggestionProviders.length);
        m_currentQueryObserver.setCurrentQuery(m_currentQuery);

        for(SuggestionProvider provider : m_suggestionProviders){
            provider.getSuggestions(m_currentQuery);
        }


        QueryPerformedCallback[] callbacks = new QueryPerformedCallback[m_suggestionPerformedCallbacks.size()];
        m_suggestionPerformedCallbacks.toArray(callbacks);
        for(QueryPerformedCallback suggestionPerformedHandler : m_suggestionPerformedCallbacks){
            suggestionPerformedHandler.onQuery(text);
        }
    }

    @Override
    public CharSequence getCurrentQuery(){
        return m_queryDisplay.getQuery();
    }
}
