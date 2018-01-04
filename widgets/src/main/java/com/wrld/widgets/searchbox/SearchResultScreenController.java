package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.api.SearchProvider;
import com.wrld.widgets.searchbox.api.SuggestionProvider;
import com.wrld.widgets.ui.UiScreenController;
import com.wrld.widgets.ui.UiScreenMemento;
import com.wrld.widgets.ui.UiScreenMementoOriginator;

import java.util.ArrayList;

class SearchResultScreenController implements UiScreenController, UiScreenMementoOriginator<SearchResultScreenVisibilityState> {

    private LayoutInflater m_inflater;

    private ViewGroup m_searchResultContainer;
    private ViewGroup m_autoCompleteResultContainer;

    private ArrayList<PaginatedSearchResultsController> m_searchResultControllers;
    private ArrayList<SuggestionSearchResultController> m_suggestionControllers;

    private Animation m_showAnim;
    private Animation m_hideAnim;

    private ScreenState m_screenState;

    public ScreenState getScreenState() { return m_screenState; }

    private SearchModuleController m_searchModuleMediator;

    private Context m_context;

    SearchResultScreenController(View resultSetsContainer, SearchModuleController searchModuleMediator){
        m_context = resultSetsContainer.getContext();
        m_inflater = LayoutInflater.from(resultSetsContainer.getContext());
        m_searchResultContainer = (ViewGroup)resultSetsContainer.findViewById(R.id.searchbox_search_results_container);
        m_autoCompleteResultContainer = (ViewGroup)resultSetsContainer.findViewById(R.id.searchbox_autocomplete_container);

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

        m_searchResultControllers = new ArrayList<PaginatedSearchResultsController>();
        m_suggestionControllers = new ArrayList<SuggestionSearchResultController>();
        m_screenState = ScreenState.GONE;
    }

    public void removeAllSearchProviderViews(){
        m_searchResultContainer.removeAllViews();
        m_searchResultControllers.clear();
    }

    public void removeAllAutocompleteProviderViews(){
        m_autoCompleteResultContainer.removeAllViews();
        m_suggestionControllers.clear();
    }

    public SearchResultsController inflateViewForSearchProvider(
            SearchProvider searchProvider,
            SearchResultSet resultSet
            ){
        // Cannot add view here with flag as we need to specify the index for layout to work
        View setView = m_inflater.inflate(R.layout.search_result_set, m_searchResultContainer, false);
        m_searchResultContainer.addView(setView, m_searchResultControllers.size());
        View setContent = setView.findViewById(R.id.searchbox_set_content);
        ListView listView = (ListView) setContent.findViewById(R.id.searchbox_set_result_list);

        PaginatedSearchResultsController resultsController = new PaginatedSearchResultsController(
                setView,
                resultSet,
                searchProvider.getResultViewFactory(),
                m_context.getString(R.string.search_shared_results_info, searchProvider.getTitle(), "%d")
        );

        m_searchResultControllers.add(resultsController);
        listView.setAdapter(resultsController);
        listView.setOnItemClickListener(onResultClickListener(resultSet));
        return resultsController;
    }

    public SearchResultsController inflateViewForAutoCompleteProvider(
            SuggestionProvider suggestionProvider,
            SearchResultSet resultSet){
        // Cannot add view here with flag as we need to specify the index for layout to work
        View setView = m_inflater.inflate(R.layout.search_suggestion_set, m_autoCompleteResultContainer, false);
        m_autoCompleteResultContainer.addView(setView, m_suggestionControllers.size());
        ListView listView = (ListView) setView.findViewById(R.id.searchbox_set_result_list);

        final SuggestionSearchResultController resultsController = new SuggestionSearchResultController(
                suggestionProvider.getSuggestionTitleFormatting(), setView, resultSet, suggestionProvider.getSuggestionViewFactory());

        m_suggestionControllers.add(resultsController);
        listView.setAdapter(resultsController);
        listView.setOnItemClickListener(onSuggestionClickListener(resultSet));
        return resultsController;
    }

    public void showResults(){
        hideSuggestionSets();

        m_searchResultContainer.setVisibility(View.VISIBLE);

        for(PaginatedSearchResultsController searchResultController : m_searchResultControllers){
            searchResultController.searchStarted();
        }
    }

    public void showAutoComplete(String text){
        m_autoCompleteResultContainer.setVisibility(View.VISIBLE);
        m_searchResultContainer.setVisibility(View.GONE);

        for(SuggestionSearchResultController suggestionController : m_suggestionControllers){
            suggestionController.updateTitle(text);
        }
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
        for(SuggestionSearchResultController suggestionController : m_suggestionControllers){
            suggestionController.hide();
        }
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

    private AdapterView.OnItemClickListener onSuggestionClickListener (final SearchResultSet resultSet){
        final UiScreenController selfAsUiScreenController = this;
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_searchModuleMediator.autocompleteSelection(selfAsUiScreenController, resultSet.getResult(position));
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
