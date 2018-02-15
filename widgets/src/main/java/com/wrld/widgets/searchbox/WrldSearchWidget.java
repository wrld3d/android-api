package com.wrld.widgets.searchbox;

import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.SearchProvider;
import com.wrld.widgets.searchbox.model.SuggestionProvider;
import com.wrld.widgets.searchbox.model.MenuGroup;
import com.wrld.widgets.searchbox.model.ObservableSearchQueryModel;
import com.wrld.widgets.searchbox.model.ObservableSearchResultsModel;
import com.wrld.widgets.searchbox.model.ObservableSuggestionQueryModel;
import com.wrld.widgets.searchbox.model.SearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;
import com.wrld.widgets.searchbox.model.SearchModel;
import com.wrld.widgets.searchbox.model.SearchWidgetSuggestionModel;
import com.wrld.widgets.searchbox.view.MenuViewController;
import com.wrld.widgets.searchbox.view.SearchResultsController;
import com.wrld.widgets.searchbox.view.SearchViewController;
import com.wrld.widgets.searchbox.view.SearchViewFocusObserver;
import com.wrld.widgets.searchbox.view.SuggestionResultsController;


public class WrldSearchWidget extends Fragment {

    private SearchResultsModel m_searchResultsModel;
    private SearchResultsModel m_suggestionResultsModel;
    private SearchModel m_searchModel;
    private SearchWidgetSuggestionModel m_suggestionModel;
    private SearchViewController m_searchViewController;
    private SuggestionResultsController m_searchSuggestionResultsController;
    private SearchResultsController m_searchResultsController;

    private SearchWidgetMenuModel m_menuModel;
    private MenuViewController m_menuViewController;

    public WrldSearchWidget() {
        super();
    }

    public void addSearchProvider(SearchProvider searchProvider)
    {
        m_searchModel.addSearchProvider(searchProvider);
    }

    public void removeSearchProvider(SearchProvider searchProvider)
    {
        m_searchModel.removeSearchProvider(searchProvider);
    }

    public void addSuggestionProvider(SuggestionProvider suggestionProvider)
    {
        m_suggestionModel.addSuggestionProvider(suggestionProvider);
    }

    public void removeSuggestionProvider(SuggestionProvider suggestionProvider)
    {
        m_suggestionModel.removeSuggestionProvider(suggestionProvider);
    }

    public void doSearch(String queryString, Object queryContext) {
        m_searchModel.doSearch(queryString, queryContext);
    }

    public void setSearchableInfo(SearchableInfo searchableInfo) {
        m_searchViewController.setSearchableInfo(searchableInfo);
    }

    public ObservableSearchResultsModel getSearchResultsModel() {
        return m_searchResultsModel;
    }

    public ObservableSearchResultsModel getSuggestionResultsModel() {
        return m_suggestionResultsModel;
    }

    public ObservableSearchQueryModel getSearchQueryModel() {
        return m_searchModel;
    }

    public ObservableSuggestionQueryModel getSuggestionQueryModel() {
        return m_suggestionModel;
    }

    public void openMenu() {
        m_menuViewController.open();
    }

    public void closeMenu() {
        m_menuViewController.close();
    }

    public void addMenuGroup(MenuGroup group) {
        m_menuModel.addMenuGroup(group);
    }

    public void removeMenuGroup(MenuGroup group) { m_menuModel.removeMenuGroup(group); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_layout, container, false);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        //m_numSuggestions = a.getInteger(R.styleable.SearchModule_maxSuggestions, 3);
        //m_maxResults  = a.getInteger(R.styleable.SearchModule_maxResults, 3);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseMenu();
        initialiseSearch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        m_searchViewController.clean();
        m_searchResultsController.clean();
        m_searchSuggestionResultsController.clean();
    }

    private void initialiseSearch() {
        m_searchResultsModel = new SearchResultsModel();
        m_searchModel = new SearchModel(m_searchResultsModel);
        m_suggestionResultsModel = new SearchResultsModel();
        m_suggestionModel = new SearchWidgetSuggestionModel(m_suggestionResultsModel);

        SearchView searchView = (SearchView)getView().findViewById(R.id.searchbox_search_searchview);
        View suggestionResultsViewContainer = getView().findViewById(R.id.searchbox_autocomplete_container);
        View searchResultsViewContainer = getView().findViewById(R.id.searchbox_search_results_container);
        View noResultsViewContainer = getView().findViewById(R.id.searchbox_no_results_container);
        View spinnerView = getView().findViewById(R.id.searchbox_search_spinner_container);

        SearchViewFocusObserver m_searchViewFocusObserver = new SearchViewFocusObserver(searchView);
        m_searchViewController = new SearchViewController(m_searchModel,
                m_suggestionModel,
                searchView,
                m_searchViewFocusObserver,
                spinnerView);
        m_searchSuggestionResultsController = new SuggestionResultsController(
                m_suggestionModel,
                m_suggestionResultsModel,
                m_searchResultsModel,
                suggestionResultsViewContainer,
                searchView,
                m_searchViewFocusObserver);

        m_searchResultsController = new SearchResultsController(
                m_searchModel,
                m_searchResultsModel,
                searchResultsViewContainer,
                searchView,
                m_searchViewFocusObserver,
                noResultsViewContainer);
    }

    private void initialiseMenu() {

        ImageButton openMenuButtonView = (ImageButton)getView().findViewById(R.id.searchbox_search_menu);
        View menuView = ((ViewStub)getView().findViewById(R.id.searchbox_menu_container_stub)).inflate();

        m_menuModel = new SearchWidgetMenuModel();
        m_menuViewController = new MenuViewController(m_menuModel, menuView, openMenuButtonView);
    }
}
