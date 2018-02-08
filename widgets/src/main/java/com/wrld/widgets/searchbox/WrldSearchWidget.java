package com.wrld.widgets.searchbox;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.ISearchProvider;
import com.wrld.widgets.searchbox.model.ISuggestionProvider;
import com.wrld.widgets.searchbox.model.MenuGroup;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;
import com.wrld.widgets.searchbox.model.SearchWidgetSuggestionModel;
import com.wrld.widgets.searchbox.view.SearchResultsController;
import com.wrld.widgets.searchbox.view.MenuViewController;
import com.wrld.widgets.searchbox.view.SearchViewController;
import com.wrld.widgets.searchbox.view.SuggestionResultsController;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;


public class WrldSearchWidget extends Fragment {

    private SearchWidgetSearchModel m_searchModel;
    private SearchWidgetSuggestionModel m_suggestionModel;
    private SearchView m_searchView;
    private SearchViewController m_searchViewController;
    private SuggestionResultsController m_searchSuggestionResultsController;
    private SearchResultsController m_searchResultsController;

    private SearchWidgetMenuModel m_menuModel;
    private MenuViewController m_menuViewController;

    public WrldSearchWidget() {
        super();
    }

    public void addSearchProvider(ISearchProvider searchProvider)
    {
        m_searchModel.addSearchProvider(searchProvider);
    }

    public void addSuggestionProvider(ISuggestionProvider suggestionProvider)
    {
        m_suggestionModel.addSuggestionProvider(suggestionProvider);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_layout, container, false);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        // Grab attributes here.

        //m_numSuggestions = a.getInteger(R.styleable.SearchModule_maxSuggestions, 3);
        //m_maxResults  = a.getInteger(R.styleable.SearchModule_maxResults, 3);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        m_searchView = (SearchView)getView().findViewById(R.id.searchbox_search_searchview);
        ListView suggestionResultsView = (ListView)getView().findViewById(R.id.searchbox_autocomplete_container);
        ListView resultsView = (ListView)getView().findViewById(R.id.searchbox_search_results_container);

        m_searchModel = new SearchWidgetSearchModel();
        m_suggestionModel = new SearchWidgetSuggestionModel();

        m_searchViewController = new SearchViewController(m_searchModel, m_suggestionModel, m_searchView);
        m_searchSuggestionResultsController = new SuggestionResultsController(
                m_suggestionModel,
                suggestionResultsView,
                m_searchView);

        m_searchResultsController = new SearchResultsController(
                m_searchModel,
                resultsView);

        ImageButton openMenuButtonView = (ImageButton)getView().findViewById(R.id.searchbox_search_menu);
        View menuView = ((ViewStub)getView().findViewById(R.id.searchbox_menu_container_stub)).inflate();

        m_menuModel = new SearchWidgetMenuModel();
        m_menuViewController = new MenuViewController(m_menuModel, menuView, openMenuButtonView);
    }

    public void doSearch() {

    }

    public void repeatSearch() {

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


}
