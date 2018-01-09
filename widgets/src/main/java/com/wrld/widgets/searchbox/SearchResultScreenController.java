package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.events.QueryResultsReadyCallback;
import com.wrld.widgets.ui.UiScreenController;
import com.wrld.widgets.ui.UiScreenMemento;
import com.wrld.widgets.ui.UiScreenMementoOriginator;

import java.util.ArrayList;

class SearchResultScreenController implements UiScreenController, UiScreenMementoOriginator<SearchResultScreenVisibilityState> {

    private LayoutInflater m_inflater;

    private ExpandableListView m_searchResultContainer;
    private ListView m_autoCompleteResultContainer;

    private ExpandableSearchResultsController m_searchResultController;
    private SuggestionSearchResultController m_suggestionController;

    private Animation m_showAnim;
    private Animation m_hideAnim;

    private ScreenState m_screenState;

    public ScreenState getScreenState() { return m_screenState; }

    private SearchModuleController m_searchModuleMediator;

    private Context m_context;

    SearchResultScreenController(
            View resultSetsContainer,
            SearchModuleController searchModuleMediator,
            SetCollection searchResultSetCollection,
            SetCollection suggestionSetCollection,
            CurrentQueryObserver currentQueryObserver){
        m_context = resultSetsContainer.getContext();
        m_inflater = LayoutInflater.from(resultSetsContainer.getContext());
        m_searchResultContainer = (ExpandableListView) resultSetsContainer.findViewById(R.id.searchbox_search_results_container);
        m_autoCompleteResultContainer = (ListView)resultSetsContainer.findViewById(R.id.searchbox_autocomplete_container);

        m_searchModuleMediator = searchModuleMediator;

        m_showAnim = new Animation(){
            @Override
            public void start() {
                super.start();
                m_screenState = ScreenState.VISIBLE;
            }
        };
        m_hideAnim = new Animation(){
            @Override
            public void start() {
                super.start();
                m_searchResultContainer.setVisibility(View.GONE);
                m_autoCompleteResultContainer.setVisibility(View.GONE);

                m_screenState = ScreenState.GONE;
            }
        };

        m_searchResultController = new ExpandableSearchResultsController(m_searchResultContainer, searchResultSetCollection);
        m_suggestionController = new SuggestionSearchResultController(m_autoCompleteResultContainer, suggestionSetCollection);
        m_autoCompleteResultContainer.setOnItemClickListener(onSuggestionClickListener(suggestionSetCollection));

        currentQueryObserver.addSuggestionsReturnedCallback(new QueryResultsReadyCallback() {
            @Override
            public void onQueryCompleted(SearchResult[] returnedResults) {
                m_suggestionController.refreshContent();
            }
        });

        m_screenState = ScreenState.GONE;
    }

    public void setSearchResultViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_searchResultController.setViewFactories(factories);
    }

    public void setSuggestionViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_suggestionController.setViewFactories(factories);
    }

    public void showResults(){
        hideSuggestionSets();

        m_searchResultContainer.setVisibility(View.VISIBLE);
        m_searchResultController.searchStarted();
    }

    public void showAutoComplete(String text){
        m_autoCompleteResultContainer.setVisibility(View.VISIBLE);
        m_searchResultContainer.setVisibility(View.GONE);
    }

    @Override
    public Animation transitionToVisible() {
        m_showAnim.reset();
        return m_showAnim;
    }

    @Override
    public Animation transitionToGone() {
        m_hideAnim.reset();
        return m_hideAnim;
    }

    private void hideSuggestionSets(){
        m_autoCompleteResultContainer.setVisibility(View.GONE);
    }

    private AdapterView.OnItemClickListener onResultClickListener (final SearchResultSet resultSet){
        final UiScreenController selfAsUiScreenController = this;
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_searchModuleMediator.focusOnResult(selfAsUiScreenController, resultSet.getResult(position));
            }
        };
    }

    private AdapterView.OnItemClickListener onSuggestionClickListener (final SetCollection resultSet){
        final UiScreenController selfAsUiScreenController = this;
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_searchModuleMediator.autocompleteSelection(selfAsUiScreenController, resultSet.getResultAtIndex(position));
            }
        };
    }

    @Override
    public UiScreenMemento<SearchResultScreenVisibilityState> generateMemento() {
        return new SearchResultScreenVisibilityState(
                m_searchResultContainer.getVisibility(),
                m_autoCompleteResultContainer.getVisibility(),
                m_screenState);
    }

    @Override
    public void resetTo(UiScreenMemento<SearchResultScreenVisibilityState> memento) {
        memento.getState().apply(m_searchResultContainer, m_autoCompleteResultContainer);
        m_screenState = memento.getState().getScreenState();
    }
}
