package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.animation.Animation;

import com.wrld.widgets.R;
import com.wrld.widgets.ui.UiScreenController;
import com.wrld.widgets.ui.UiScreenStateList;

import java.util.ArrayList;
import java.util.Stack;

class SearchModuleController {

    private SearchQueryHandler m_searchQueryHandler;
    private SearchController m_searchController;
    private SearchResultScreenController m_searchResultScreenController;
    private SearchMenuController m_searchMenuController;

    private ArrayList<UiScreenController> m_allScreens;
    private Stack<UiScreenStateList> m_uiScreenHistory;

    public void setSearchQueryHandler(SearchQueryHandler queryHandler){
        m_searchQueryHandler = queryHandler;
        m_uiScreenHistory = new Stack<UiScreenStateList>();
        m_allScreens = new ArrayList<UiScreenController>();
    }

    public void setQueryBoxController(SearchController searchController) {
        m_searchController = searchController;
        m_allScreens.add(searchController);
    }

    public void setSearchResultsSetController(SearchResultScreenController searchResultScreenController) {
        m_searchResultScreenController = searchResultScreenController;
        m_allScreens.add(searchResultScreenController);
    }

    public void setSearchMenuController(SearchMenuController searchMenuController) {
        m_searchMenuController = searchMenuController;
        m_allScreens.add(searchMenuController);
    }

    public void showQueryBox(UiScreenController caller) {
        doShowElement(m_searchController);
    }

    public void hideResults(UiScreenController caller){
        doHideElement(m_searchResultScreenController);
    }

    public void doSearch(UiScreenController caller, String query){

        doShowElement(m_searchController);
        doShowElement(m_searchResultScreenController);
        if(caller != m_searchController){
            doHideElement(caller);
            m_searchController.setQuery(query);
        }
        m_searchResultScreenController.showResults();
        m_searchQueryHandler.searchFor(query);
    }

    public void doAutocomplete(String text){
        m_searchQueryHandler.getSuggestionsFor(text);

        m_searchResultScreenController.showAutoComplete(text);
        doShowElement(m_searchResultScreenController);
    }

    public void clearSearch(UiScreenController caller){
        m_searchController.clear();
        doHideElement(m_searchResultScreenController);
    }

    public SearchResultsController addSearchProvider(SearchProvider searchProvider, SearchResultSet searchResultSet) {
        return m_searchResultScreenController.inflateViewForSearchProvider(
                searchProvider,
                searchResultSet);
    }

    public void removeAllSearchProviders(){
        m_searchResultScreenController.removeAllSearchProviderViews();
    }

    public SearchResultsController addSuggestionProvider(SuggestionProvider suggestionProvider, SearchResultSet searchResultSet){
       return m_searchResultScreenController.inflateViewForAutoCompleteProvider(
               suggestionProvider,
               searchResultSet);
    }

    public void removeAllSuggestionProviders(){
        m_searchResultScreenController.removeAllAutocompleteProviderViews();
    }

    //TODO public void focusOnResult(SearchModuleMediatorElement caller, SearchResult result){}

    public void showMenu(UiScreenController caller) {
        m_uiScreenHistory.push(new UiScreenStateList(m_allScreens));
        doHideElement(m_searchController);
        doHideElement(m_searchResultScreenController);
        doShowElement(m_searchMenuController);
    }

    private void doShowElement(UiScreenController element){
        if(element.getScreenState() == UiScreenController.ScreenState.GONE){
            Animation showAnim = element.transitionToVisible();
            showAnim.start();
        }
    }
    private void doHideElement(UiScreenController element){
        if(element.getScreenState() == UiScreenController.ScreenState.VISIBLE){
            Animation showAnim = element.transitionToGone();
            showAnim.start();
        }
    }

    public void back(){
        if(m_uiScreenHistory.size() > 0){
            m_uiScreenHistory.pop().resetTo();
        }
    }

    public void showDefaultView(){
        doShowElement(m_searchController);
        doHideElement(m_searchResultScreenController);
        doHideElement(m_searchMenuController);
        m_uiScreenHistory.clear();
    }
}
