package com.wrld.widgets.searchbox;

import android.view.animation.Animation;

import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SearchQueryHandler;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.searchbox.api.events.SearchResultSelectedCallback;
import com.wrld.widgets.ui.UiScreenController;
import com.wrld.widgets.ui.UiScreenMementoOriginator;
import com.wrld.widgets.ui.UiScreenStateList;

import java.util.ArrayList;
import java.util.Stack;

class SearchModuleController {

    private SearchQueryHandler m_searchQueryHandler;
    private SearchController m_searchController;
    private SearchResultScreenController m_searchResultScreenController;
    private SearchMenuController m_searchMenuController;

    private ArrayList<UiScreenMementoOriginator> m_allScreens;
    private Stack<UiScreenStateList> m_uiScreenHistory;

    private ArrayList<SearchResultSelectedCallback> m_searchResultSelectedCallbacks;
    private ArrayList<SearchResultSelectedCallback> m_suggestionSelectedCallbacks;

    public SearchModuleController(){
        m_searchResultSelectedCallbacks = new ArrayList<SearchResultSelectedCallback> ();
        m_suggestionSelectedCallbacks = new ArrayList<SearchResultSelectedCallback> ();
    }

    public void setSearchQueryHandler(SearchQueryHandler queryHandler){
        m_searchQueryHandler = queryHandler;
        m_uiScreenHistory = new Stack<UiScreenStateList>();
        m_allScreens = new ArrayList<UiScreenMementoOriginator>();
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
            m_searchController.setQueryDisplayString(query);
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

    public void setSearchResultViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_searchResultScreenController.setSearchResultViewFactories(factories);
    }

    public void setSuggestionViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_searchResultScreenController.setSuggestionViewFactories(factories);
    }

    public void autocompleteSelection(UiScreenController caller, SearchResult result){
        m_searchController.clear();
        doSearch(caller, result.getTitle());
        SearchResultSelectedCallback callbacks [] = new SearchResultSelectedCallback[m_suggestionSelectedCallbacks.size()];
        m_suggestionSelectedCallbacks.toArray(callbacks);
        for(SearchResultSelectedCallback callback : callbacks){
            callback.onSelection(result);
        }
    }

    public void focusOnResult(UiScreenController caller, SearchResult result){
        SearchResultSelectedCallback callbacks [] = new SearchResultSelectedCallback[m_searchResultSelectedCallbacks.size()];
        m_searchResultSelectedCallbacks.toArray(callbacks);
        for(SearchResultSelectedCallback callback : callbacks){
            callback.onSelection(result);
        }
    }

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

    public void addSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback) {
        m_searchResultSelectedCallbacks.add(searchResultSelectedCallback);
    }

    public void removeSearchResultSelectedCallback(SearchResultSelectedCallback searchResultSelectedCallback) {
        m_searchResultSelectedCallbacks.remove(searchResultSelectedCallback);
    }

    public void addSuggestionSelectedCallback(SearchResultSelectedCallback suggestionSelectedCallback) {
        m_suggestionSelectedCallbacks.add(suggestionSelectedCallback);
    }

    public void removeSuggestionSelectedCallback(SearchResultSelectedCallback suggestionSelectedCallback) {
        m_suggestionSelectedCallbacks.remove(suggestionSelectedCallback);
    }
}
