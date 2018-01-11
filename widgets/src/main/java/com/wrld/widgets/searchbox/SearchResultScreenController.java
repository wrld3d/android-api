package com.wrld.widgets.searchbox;

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

    private ExpandableListView m_searchResultContainer;
    private ListView m_suggestionResultContainer;

    private ExpandableSearchResultsController m_searchResultController;
    private SuggestionSearchResultController m_suggestionController;

    private Animation m_showAnim;
    private Animation m_hideAnim;

    private ScreenState m_screenState;

    public ScreenState getScreenState() { return m_screenState; }

    private SearchModuleController m_searchModuleMediator;

    SearchResultScreenController(
            View resultSetsContainer,
            SearchModuleController searchModuleMediator,
            SetCollection searchResultSetCollection,
            SetCollection suggestionSetCollection,
            CurrentQueryObserver currentQueryObserver){
        m_searchResultContainer = (ExpandableListView) resultSetsContainer.findViewById(R.id.searchbox_search_results_container);
        m_suggestionResultContainer = (ListView)resultSetsContainer.findViewById(R.id.searchbox_autocomplete_container);

        m_searchModuleMediator = searchModuleMediator;


        m_searchResultController = new ExpandableSearchResultsController(m_searchResultContainer, searchResultSetCollection);
        m_searchResultContainer.setOnChildClickListener(onResultClickListener(searchResultSetCollection));
        m_suggestionController = new SuggestionSearchResultController(m_suggestionResultContainer, suggestionSetCollection);
        m_suggestionResultContainer.setOnItemClickListener(onSuggestionClickListener(suggestionSetCollection));

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
                m_searchResultController.hide();
                m_suggestionController.hide();

                m_screenState = ScreenState.GONE;
            }
        };

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
        hideSuggestions();

        m_searchResultController.searchStarted();
        m_searchResultController.show();
    }

    public void showSuggestions(String text){
        m_suggestionController.show();
        m_searchResultController.hide();
    }

    private void hideSuggestions() {
        m_suggestionController.hide();
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

    private ExpandableListView.OnChildClickListener onResultClickListener (final SetCollection resultSet){
        // TODO change scope of external result exposure
        final UiScreenController selfAsUiScreenController = this;
        return new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                SearchResultSet set = resultSet.getSet(groupPosition);
                if(childPosition == set.getVisibleResultCount()){
                    if(resultSet.hasExpandedSet()){
                        resultSet.collapseResultSets();
                    }
                    else {
                        resultSet.expandResultSet(groupPosition);
                    }
                }
                else {
                    m_searchModuleMediator.focusOnResult(selfAsUiScreenController, resultSet.getResultAtIndex(groupPosition, childPosition));
                }
                return true;
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
                m_suggestionResultContainer.getVisibility(),
                m_screenState);
    }

    @Override
    public void resetTo(UiScreenMemento<SearchResultScreenVisibilityState> memento) {
        memento.getState().apply(m_searchResultContainer, m_suggestionResultContainer);
        m_screenState = memento.getState().getScreenState();
    }
}
