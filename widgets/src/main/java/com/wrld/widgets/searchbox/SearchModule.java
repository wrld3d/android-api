package com.wrld.widgets.searchbox;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

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
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SearchModule extends Fragment implements com.wrld.widgets.searchbox.api.SearchModule, SearchQueryHandler {

    private static final int SPEECH_REQUEST_CODE = 0;

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

    private Context m_context;

    public SearchModule() {
        super();

        m_searchProviders                   = new SearchProvider[0];
        m_suggestionProviders               = new SuggestionProvider[0];

        m_searchProviderSets                = new ArrayList<SearchResultSet>();
        m_suggestionProviderSets            = new ArrayList<SearchResultSet>();

        m_queryPerformedCallbacks           = new ArrayList<QueryPerformedCallback> ();
        m_suggestionPerformedCallbacks      = new ArrayList<QueryPerformedCallback> ();

        m_currentQueryObserver = new CurrentQueryObserver();
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SearchModule,
                0, 0);

        m_numSuggestions = a.getInteger(R.styleable.SearchModule_maxSuggestions, 3);
        m_maxResults  = a.getInteger(R.styleable.SearchModule_maxResults, 3);

        m_context = context;
    }

    @Override
    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {

        m_menuContent                       = new SearchMenuContent(inflater);
        m_searchModuleController            = new SearchModuleController();
        m_searchResultSetFactory            = new SearchResultSetFactory(m_numSuggestions, m_maxResults);

        View root = inflater.inflate(R.layout.search_layout, container, true);

        m_searchModuleController.setSearchQueryHandler(this);

        ViewGroup searchContainer = (ViewGroup) root.findViewById(R.id.searchbox_search_container);
        SearchController searchController = new SearchController(searchContainer, m_searchModuleController);
        m_searchModuleController.setQueryBoxController(searchController);
        m_queryDisplay = (SearchView) searchContainer.findViewById(R.id.searchbox_search_searchview);

        int searchImgId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView iv = (ImageView)m_queryDisplay.findViewById(searchImgId);
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) iv.getLayoutParams();
//        lp.setMarginStart(0);
//        iv.setLayoutParams(lp);
        iv.setLayoutParams(new LinearLayout.LayoutParams(0,0));

        int searchMicId = getResources().getIdentifier("android:id/search_voice_btn", null, null);
        iv = (ImageView)m_queryDisplay.findViewById(searchMicId);
        iv.setPadding(0, iv.getPaddingTop(), 0, iv.getPaddingBottom());

        int search_close_btn = getResources().getIdentifier("android:id/search_close_btn", null, null);
        iv = (ImageView)m_queryDisplay.findViewById(search_close_btn);
        iv.setPadding(0, iv.getPaddingTop(), 0, iv.getPaddingBottom());

        SearchManager searchManager = (SearchManager) m_context.getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = getActivity().getComponentName();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
        m_queryDisplay.setSearchableInfo(searchableInfo);

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

        int matchedTextColor = ContextCompat.getColor(m_context, R.color.searchbox_autcomplete_list_header_font_matched);

        DefaultSuggestionViewFactory suggestionViewFactory = new DefaultSuggestionViewFactory(
                R.layout.search_suggestion,
                this,
                new TextHighlighter(matchedTextColor));

        setDefaultSuggestionViewFactory(suggestionViewFactory);

        m_searchModuleController.showQueryBox(searchController);
        m_searchModuleController.hideResults(searchController);

        m_searchMenuController.setDefaultMenuContent(root.getContext());

        return root;
    }

    @Override
    public boolean handleSearchIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(query != null) {
                doSearch(query);
                return true;
            }
        }
        if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            doSearch(data.toString());
            return true;
        }
        return false;
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
