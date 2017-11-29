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

    private boolean m_searchResultsVisible;
    private boolean m_suggestionsVisible;

    private final int RESULTS_PER_PAGE = 20;

    private int m_resultsPage;

    private TextView m_searchResultInfo;
    private View m_setContainer;
    private View m_header;
    private View m_footer;
    private View m_expandControls;
    private View m_paginationControls;
    private View m_resultsView;

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

        m_searchResultsView = container.findViewById(R.id.search_result_list);
        m_searchInProgressView = container.findViewById(R.id.search_set_in_progress_view);

        m_resultsView = container.findViewById(R.id.search_result_list);
        m_inflater =inflater;

        m_resultsModel = resultsModel;
        m_suggestionsModel = suggestionsModel;
        m_suggestionsViewFactory = factorySuggestions;
        m_resultsViewFactory = viewFactoryResults;

        m_suggestionsVisible = false;
        m_searchResultsVisible = false;
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
        m_searchResultsVisible = false;
    }

    @Override
    public void showResults(boolean flag) {
        if(m_searchResultsVisible != flag) {
            m_searchResultsVisible = flag;
            m_suggestionsVisible = !flag;
            setContainerElementsVisibility();
        }

        setContainerElementsVisibility();

        if(m_searchResultsVisible){
            m_searchInProgressView.setVisibility(View.GONE);
            m_resultsPage = 0;
            refreshContent();
        }
        else{
            m_searchInProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSuggestions(boolean flag) {
        if(m_suggestionsVisible != flag) {
            m_suggestionsVisible = flag;
            m_searchResultsVisible = !flag;
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

    private void setContainerElementsVisibility(){
        int containerVisible = (m_suggestionsVisible || m_searchResultsVisible) ? View.VISIBLE : View.GONE;
        m_searchResultsView.setVisibility(containerVisible);

        setSearchControlVisibility(m_searchResultsVisible ? View.VISIBLE : View.GONE );
    }

    private void setSearchControlVisibility(int visibility){
        m_expandControls.setVisibility(visibility);
        m_header.setVisibility(visibility);
        m_footer.setVisibility(visibility);

        m_paginationControls.setVisibility(View.GONE);}


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

    public void setState(ResultSetState state){

        LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) m_setContainer.getLayoutParams();

        if(state == ResultSetState.Collapsed){
            m_expandControls.setVisibility(View.VISIBLE);
            m_paginationControls.setVisibility(View.GONE);
            m_resultsView.setVisibility(View.GONE);
            m_header.setVisibility(View.GONE);
            createAnimation(loparams.weight, 1f);
        }
        if(state == ResultSetState.Shared){
            m_expandControls.setVisibility(View.VISIBLE);
            m_paginationControls.setVisibility(View.GONE);
            m_resultsView.setVisibility(View.VISIBLE);
            m_header.setVisibility(View.VISIBLE);
            createAnimation(loparams.weight, 1f);
        }
        if(state == ResultSetState.Expanded){
            m_expandControls.setVisibility(View.GONE);
            m_paginationControls.setVisibility(View.VISIBLE);
            m_resultsView.setVisibility(View.VISIBLE);
            m_header.setVisibility(View.VISIBLE);
            createAnimation(loparams.weight, 0.1f);
        }
    }

    private void createAnimation(float startWeight, float deltaWeight)
    {
        ExpandAnimation anim = new ExpandAnimation(m_setContainer, startWeight, deltaWeight);
        anim.setDuration(500);
        m_setContainer.startAnimation(anim);
    }
}
