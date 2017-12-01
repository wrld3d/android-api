// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.wrld.widgets.R;

class DefaultSearchResultsContainer extends BaseAdapter implements ListAdapter, PaginatedSearchResultsContainer {

    private LayoutInflater m_inflater;

    private SearchResultSet m_resultsModel;
    private SearchResultSet m_suggestionsModel;
    private SearchResultViewFactory m_resultsViewFactory;
    private SearchResultViewFactory m_suggestionsViewFactory;

    private ResultsViewState m_resultsViewState;
    private boolean m_suggestionsVisible;

    private final int RESULTS_PER_PAGE = 20;

    private int m_resultsPage;

    private TextView m_searchResultInfo;
    private View m_setContainer;
    private View m_header;
    private View m_footer;
    private View m_expandControls;
    private View m_paginationControls;
    private View[] m_paginationButtons;

    private View m_searchInProgressView;
    private View m_searchResultsView;


    private final String RESULTS_INFO = "%d-%d of %d";

    public DefaultSearchResultsContainer(LayoutInflater inflater, View container,
                                         SearchResultSet resultsModel, SearchResultViewFactory viewFactoryResults ,
                                         SearchResultSet suggestionsModel , SearchResultViewFactory factorySuggestions) {

        m_setContainer = container;
        m_header = container.findViewById(R.id.search_set_header);
        m_footer = container.findViewById(R.id.search_set_footer);
        m_searchResultInfo = (TextView) container.findViewById(R.id.search_pagination_results_info);
        m_expandControls = container.findViewById(R.id.expand_controls);
        m_paginationControls = container.findViewById(R.id.pagination_controls);
        m_paginationButtons = new View[2];
        m_paginationButtons[0] = container.findViewById(R.id.search_pagination_prev);
        m_paginationButtons[1] = container.findViewById(R.id.search_pagination_next);

        m_searchResultsView = container.findViewById(R.id.search_result_list);
        m_searchInProgressView = container.findViewById(R.id.search_set_in_progress_view);

        m_inflater =inflater;

        m_resultsModel = resultsModel;
        m_suggestionsModel = suggestionsModel;
        m_suggestionsViewFactory = factorySuggestions;
        m_resultsViewFactory = viewFactoryResults;

        m_suggestionsVisible = false;
        m_resultsViewState = ResultsViewState.Hidden;
    }

    @Override
    /**
     * returns the number of ListView elements to be rendered on the current page of results
     */
    public int getCount() {
        if(m_suggestionsVisible){
            return m_suggestionsModel.getResultCount();
        }

        int notPagedOverCount = m_resultsModel.getResultCount() - (m_resultsPage * RESULTS_PER_PAGE);
        int onScreenResults = Math.min(RESULTS_PER_PAGE, notPagedOverCount);

        return onScreenResults;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return m_suggestionsVisible ? 1 : 0;
    }

    @Override
    public Object getItem(int position) {
        return  getResultForState(position);
    }

    @Override
    public long getItemId(int position) {
        if(m_suggestionsVisible) {
            return position;
        }

        int actualResultPosition = position + m_resultsPage * RESULTS_PER_PAGE;
        return actualResultPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

            SearchResultViewFactory factory = (m_suggestionsVisible ? m_suggestionsViewFactory : m_resultsViewFactory);

            convertView = factory.makeSearchResultView(m_inflater, getResultForState(position), parent);

            SearchResultViewHolder viewHolder = factory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        ((SearchResultViewHolder)convertView.getTag()).populate(getResultForState(position));

        return convertView;
    }

    private SearchResult getResultForState(int position) {
        if(m_suggestionsVisible) {
            return m_suggestionsModel.getResult(position);
        }

        int actualResultPosition = position + m_resultsPage * RESULTS_PER_PAGE;
        return m_resultsModel.getResult(actualResultPosition);
    }

    @Override
    public void searchStarted() {
        m_searchInProgressView.setVisibility(View.VISIBLE);
        m_searchResultsView.setVisibility(View.GONE);
        m_resultsViewState = ResultsViewState.Hidden;
    }

    @Override
    public void setResultsVisibility(boolean resultsVisible) {
        if(resultsVisible) {
            if(!searchResultsCurrentlyVisible()) {
                m_resultsViewState = ResultsViewState.Shared;
                m_suggestionsVisible = false;
                m_resultsPage = 0;
                setContainerElementsVisibility();
                refreshContent();
            }
        }
        else {
            if(searchResultsCurrentlyVisible()) {
                m_resultsViewState = ResultsViewState.Hidden;
                setContainerElementsVisibility();
            }
        }
    }

    @Override
    public void setSuggestionsVisilility(boolean suggestionsVisible) {
        if(m_suggestionsVisible != suggestionsVisible) {
            m_suggestionsVisible = suggestionsVisible;
            if(m_suggestionsVisible){
                m_resultsViewState = ResultsViewState.Hidden;
            }
            setContainerElementsVisibility();
            refreshContent();
        }
    }

    @Override
    public void refreshContent() {
        notifyDataSetChanged();

        int resultsStart = m_resultsPage * RESULTS_PER_PAGE;
        int resultsEnd = Math.min(resultsStart + RESULTS_PER_PAGE, m_resultsModel.getResultCount());

        m_searchResultInfo.setText(String.format(RESULTS_INFO, resultsStart, resultsEnd, m_resultsModel.getResultCount()));
    }

    private boolean searchResultsCurrentlyVisible() {
        return m_resultsViewState != ResultsViewState.Hidden;
    }

    private void setContainerElementsVisibility(){
        int containerVisible = (m_suggestionsVisible || searchResultsCurrentlyVisible()) ? View.VISIBLE : View.GONE;

        m_searchResultsView.setVisibility(containerVisible);
        m_searchInProgressView.setVisibility(View.GONE);

        configureSearchResultPaginationViews();
    }

    private void configureSearchResultPaginationViews(){

        int anyResults = m_resultsModel.getResultCount() > 0 ? View.VISIBLE : View.GONE;
        int multiplePages = m_resultsModel.getResultCount() > RESULTS_PER_PAGE ? View.VISIBLE : View.GONE;

        switch (m_resultsViewState) {
            case Collapsed:
                m_expandControls.setVisibility(anyResults);
                m_paginationControls.setVisibility(View.GONE);
                m_header.setVisibility(View.GONE);
                m_footer.setVisibility(anyResults);
                break;
            case Shared:
                m_expandControls.setVisibility(anyResults);
                m_paginationControls.setVisibility(View.GONE);
                m_header.setVisibility(View.VISIBLE);
                m_footer.setVisibility(anyResults);
                break;
            case Expanded:
                m_expandControls.setVisibility(View.GONE);
                m_paginationControls.setVisibility(anyResults);
                paginationButtonVisibility(multiplePages);
                m_searchResultInfo.setVisibility(anyResults);
                m_header.setVisibility(View.VISIBLE);
                m_footer.setVisibility(anyResults);
                break;
            case Hidden:
                m_expandControls.setVisibility(View.GONE);
                m_paginationControls.setVisibility(View.GONE);
                m_header.setVisibility(View.GONE);
                m_footer.setVisibility(View.GONE);
                break;
        }
    }

    private void paginationButtonVisibility(int visibility){
        for(View view : m_paginationButtons){
            view.setVisibility(visibility);
        }
    }

    @Override
    public void nextPage() {
        if(m_resultsModel.getResultCount() > RESULTS_PER_PAGE * (m_resultsPage + 1)) {
            ++m_resultsPage;
            refreshContent();
        }
    }

    @Override
    public void previousPage() {
        if(m_resultsPage > 0) {
            --m_resultsPage;
            refreshContent();
        }
    }

    public void setResultsViewState(ResultsViewState state){
        if(state != m_resultsViewState) {
            m_resultsViewState = state;
            LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) m_setContainer.getLayoutParams();
            if (state == ResultsViewState.Collapsed) {
                createAnimation(loparams.weight, 1f);
            }
            if (state == ResultsViewState.Shared) {
                createAnimation(loparams.weight, 1f);
            }
            if (state == ResultsViewState.Expanded) {
                createAnimation(loparams.weight, 0.1f);
            }

            setContainerElementsVisibility();
        }
    }

    private void createAnimation(float startWeight, float deltaWeight)
    {
        ExpandAnimation anim = new ExpandAnimation(m_setContainer, startWeight, deltaWeight);
        anim.setDuration(500);
        m_setContainer.startAnimation(anim);
    }
}
