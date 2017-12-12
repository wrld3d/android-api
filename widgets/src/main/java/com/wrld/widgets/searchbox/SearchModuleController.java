package com.wrld.widgets.searchbox;

import android.view.animation.Animation;

import com.wrld.widgets.ui.UiScreenController;

import java.util.ArrayList;

/*class IndexPath {
    private int m_section;
    private int m_row;
    public IndexPath(int section, int row){
        m_section = section;
        m_row = row;
    }

    public int getSection() {return m_section;}
    public int getRow() {return m_row;}
}*/

class SearchModuleController {

    private SearchQueryHandler m_searchQueryHandler;
    private SearchController m_searchController;
    private SearchResultScreenController m_searchResultScreenController;

    public void setSearchQueryHandler(SearchQueryHandler queryHandler){
        m_searchQueryHandler = queryHandler;
    }

    public void setQueryBoxController(SearchController searchController) {
        m_searchController = searchController;
    }

    public void setSearchResultsSetController(SearchResultScreenController searchResultScreenController) {
        m_searchResultScreenController = searchResultScreenController;
    }

    public void showQueryBox(UiScreenController caller) {
        doShowElement(m_searchController);
    }

    public void hideResults(UiScreenController caller){
        doHideElement(m_searchResultScreenController);
    }

    public void doSearch(UiScreenController caller, String query){
        m_searchQueryHandler.searchFor(query);

        doShowElement(m_searchController);
        doShowElement(m_searchResultScreenController);
        if(caller != m_searchController){
            doHideElement(caller);
        }
        m_searchResultScreenController.showResults();
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

    public void setSearchProviders(SearchProvider[] searchProviders, SearchResultSetFactory searchResultSetFactory) {
        m_searchResultScreenController.removeAllSearchProviderViews();
        for(final SearchProvider searchProvider : searchProviders){
            SearchResultSet searchResultSet = searchResultSetFactory.createResultSetForSearchProvider(searchProvider);
            SearchResultsController searchResultsController =
                    m_searchResultScreenController.inflateViewForSearchProvider(
                            searchResultSet, searchProvider.getResultViewFactory());
            searchResultSet.addOnResultChangedHandler(searchResultsController.getUpdateCallback());
        }
    }

    public void setSuggestionProviders(SuggestionProvider[] suggestionProviders, SearchResultSetFactory searchResultSetFactory){
        m_searchResultScreenController.removeAllAutocompleteProviderViews();
        for(final SuggestionProvider suggestionProvider : suggestionProviders){
            SearchResultSet searchResultSet = searchResultSetFactory.createResultSetForSuggestionProvider(suggestionProvider);
            SearchResultsController searchResultsController =
                    m_searchResultScreenController.inflateViewForAutoCompleteProvider(
                            searchResultSet, suggestionProvider.getSuggestionViewFactory());
            searchResultSet.addOnResultChangedHandler(searchResultsController.getUpdateCallback());
        }
    }

    //TODO public void focusOnResult(SearchModuleMediatorElement caller, SearchResult result){}
    //TODO public void showMenu(SearchModuleMediatorElement caller) {}
    //TODO public void doMenuClick(IndexPath indexPath) {}

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
}
