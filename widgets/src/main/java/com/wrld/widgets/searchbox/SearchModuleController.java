package com.wrld.widgets.searchbox;

import android.view.animation.Animation;

import com.wrld.widgets.ui.ExpandableListViewIndex;
import com.wrld.widgets.ui.UiScreenController;

import java.util.Stack;

class SearchModuleController {

    private SearchQueryHandler m_searchQueryHandler;
    private SearchController m_searchController;
    private SearchResultScreenController m_searchResultScreenController;
    private SearchMenuController m_searchMenuController;

    private Stack<UiScreenController> m_uiScreenHistory;
    UiScreenController m_currentUiScreen;

    public void setSearchQueryHandler(SearchQueryHandler queryHandler){
        m_searchQueryHandler = queryHandler;
        m_uiScreenHistory = new Stack<UiScreenController>();
    }

    public void setQueryBoxController(SearchController searchController) {
        m_searchController = searchController;
    }

    public void setSearchResultsSetController(SearchResultScreenController searchResultScreenController) {
        m_searchResultScreenController = searchResultScreenController;
    }

    public void setSearchMenuController(SearchMenuController searchMenuController) {
        m_searchMenuController = searchMenuController;
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

        m_searchResultScreenController.showAutoComplete();
        doShowElement(m_searchResultScreenController);
    }

    public void clearSearch(UiScreenController caller){
        m_searchController.clear();
        doHideElement(m_searchResultScreenController);
    }

    public SearchResultsController addSearchProvider(SearchProvider searchProvider, SearchResultSet searchResultSet) {
        return m_searchResultScreenController.inflateViewForSearchProvider(
                        searchResultSet, searchProvider.getResultViewFactory());
    }

    public void removeAllSearchProviders(){
        m_searchResultScreenController.removeAllSearchProviderViews();
    }

    public SearchResultsController addSuggestionProvider(SuggestionProvider suggestionProvider, SearchResultSet searchResultSet){
       return m_searchResultScreenController.inflateViewForAutoCompleteProvider(
                            searchResultSet, suggestionProvider.getSuggestionViewFactory());
    }

    public void removeAllSuggestionProviders(){
        m_searchResultScreenController.removeAllAutocompleteProviderViews();
    }

    //TODO public void focusOnResult(SearchModuleMediatorElement caller, SearchResult result){}

    public void showMenu(UiScreenController caller) {
        m_uiScreenHistory.push(caller);
        doHideElement(caller);
        doShowElement(m_searchMenuController);
        m_currentUiScreen = m_searchMenuController;
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
            doHideElement(m_currentUiScreen);
            m_currentUiScreen = m_uiScreenHistory.pop();
            doShowElement(m_currentUiScreen);
        }
    }
}
