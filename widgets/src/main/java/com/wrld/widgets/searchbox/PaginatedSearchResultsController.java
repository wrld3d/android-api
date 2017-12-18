package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.wrld.widgets.R;

class PaginatedSearchResultsController extends BaseAdapter implements SearchResultsController {

    enum ResultsViewState {
        Collapsed,
        Shared,
        Expanded,
        Hidden
    }

    private LayoutInflater m_inflater;

    private SearchResultSet m_searchResultSet;
    private SearchResultViewFactory m_resultsViewFactory;

    private ResultsViewState m_resultsViewState;

    private final int RESULTS_PER_PAGE = 20;

    private int m_resultsPage;

    private TextView m_searchResultSharedInfoView;
    private TextView m_searchResultExpandedPageInfoView;
    private TextView m_searchResultExpandedResultCountView;
    private View m_setContainer;
    private View m_footer;
    private View m_expandControls;
    private View m_paginationControls;
    private View[] m_paginationButtons;

    private View m_searchInProgressView;
    private View m_searchResultsView;

    private String m_expandButtonTextFormatting;
    private String m_expandedTextFormatting;
    private String m_expandedResultsCountTextFormatting;

    public PaginatedSearchResultsController(View container,
                                            SearchResultSet resultSet,
                                            SearchResultViewFactory viewFactory,
                                            String expandButtonTextFormatting) {

        Context context = container.getContext();
        m_inflater = LayoutInflater.from(context);

        m_expandButtonTextFormatting = expandButtonTextFormatting;
        m_expandedTextFormatting = context.getString(R.string.search_expanded_results_info, "%d");
        m_expandedResultsCountTextFormatting = context.getString(R.string.search_expanded_results_count, "%d");

        m_setContainer = container;
        m_searchResultSet = resultSet;

        m_footer = container.findViewById(R.id.searchbox_set_footer);
        m_searchResultSharedInfoView = (TextView) container.findViewById(R.id.searchbox_set_expand_info);
        m_searchResultExpandedPageInfoView = (TextView) container.findViewById(R.id.searchbox_set_pagination_results_info);
        m_searchResultExpandedResultCountView = (TextView) container.findViewById(R.id.searchbox_set_expanded_results_count);
        m_expandControls = container.findViewById(R.id.searchbox_set_expand_controls);
        m_paginationControls = container.findViewById(R.id.searchbox_set_pagination_controls);

        m_paginationButtons = new View[2];
        m_paginationButtons[0] = container.findViewById(R.id.searchbox_set_pagination_prev);
        m_paginationButtons[1] = container.findViewById(R.id.searchbox_set_pagination_next);

        m_searchResultsView = container.findViewById(R.id.searchbox_set_result_list);
        m_searchInProgressView = container.findViewById(R.id.searchbox_set_search_in_progress_view);

        m_resultsViewFactory = viewFactory;

        m_resultsViewState = ResultsViewState.Hidden;
    }

    @Override
    public SearchResultSet.OnResultChanged getUpdateCallback(){
        return new SearchResultSet.OnResultChanged() {
            @Override
            public void invoke() {
                m_resultsViewState = ResultsViewState.Shared;
                m_resultsPage = 0;
                setContainerElementsVisibility();
                refreshContent();
            }
        };
    }

    @Override
    /**
     * returns the number of ListView elements to be rendered on the current page of results
     */
    public int getCount() {
        int notPagedOverCount = m_searchResultSet.getResultCount() - (m_resultsPage * RESULTS_PER_PAGE);
        int onScreenResults = Math.min(RESULTS_PER_PAGE, notPagedOverCount);

        return onScreenResults;
    }

    @Override
    public Object getItem(int position) {
        return  getResult(position);
    }

    @Override
    public long getItemId(int position) {
        int actualResultPosition = position + m_resultsPage * RESULTS_PER_PAGE;
        return actualResultPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = m_resultsViewFactory.makeSearchResultView(m_inflater, getResult(position), parent);
            SearchResultViewHolder viewHolder = m_resultsViewFactory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        ((SearchResultViewHolder)convertView.getTag()).populate(getResult(position));

        return convertView;
    }

    private SearchResult getResult(int position) {
        int actualResultPosition = position + m_resultsPage * RESULTS_PER_PAGE;
        return m_searchResultSet.getResult(actualResultPosition);
    }

    public void searchStarted() {
        m_searchInProgressView.setVisibility(View.VISIBLE);
        m_searchResultsView.setVisibility(View.GONE);
        m_resultsViewState = ResultsViewState.Hidden;
        configureSearchResultPaginationViews();
    }

    @Override
    public void refreshContent() {
        notifyDataSetChanged();

        int resultsStart = m_resultsPage * RESULTS_PER_PAGE;
        int resultsEnd = Math.min(resultsStart + RESULTS_PER_PAGE, m_searchResultSet.getResultCount());
        int resultsCount = m_searchResultSet.getResultCount();
        m_searchResultSharedInfoView.setText(String.format(m_expandButtonTextFormatting, resultsCount));
        m_searchResultExpandedPageInfoView.setText(String.format(m_expandedTextFormatting, resultsStart, resultsEnd));
        m_searchResultExpandedResultCountView.setText(String.format(m_expandedResultsCountTextFormatting, resultsCount));
    }

    private boolean searchResultsCurrentlyVisible() {
        return m_resultsViewState != ResultsViewState.Hidden;
    }

    private void setContainerElementsVisibility(){
        int containerVisible = searchResultsCurrentlyVisible() ? View.VISIBLE : View.GONE;

        m_searchResultsView.setVisibility(containerVisible);
        m_searchInProgressView.setVisibility(View.GONE);

        configureSearchResultPaginationViews();
    }

    private void configureSearchResultPaginationViews(){

        int anyResults = m_searchResultSet.getResultCount() > 0 ? View.VISIBLE : View.GONE;
        int multiplePages = m_searchResultSet.getResultCount() > RESULTS_PER_PAGE ? View.VISIBLE : View.GONE;

        switch (m_resultsViewState) {
            case Collapsed:
                m_expandControls.setVisibility(anyResults);
                m_paginationControls.setVisibility(View.GONE);
                m_footer.setVisibility(anyResults);
                break;
            case Shared:
                m_expandControls.setVisibility(anyResults);
                m_paginationControls.setVisibility(View.GONE);
                m_footer.setVisibility(anyResults);
                m_searchResultSharedInfoView.setVisibility(View.VISIBLE);
                m_searchResultExpandedPageInfoView.setVisibility(View.GONE);
                m_searchResultExpandedPageInfoView.setVisibility(View.GONE);
                break;
            case Expanded:
                m_expandControls.setVisibility(View.GONE);
                m_paginationControls.setVisibility(anyResults);
                paginationButtonVisibility(multiplePages);
                m_searchResultExpandedPageInfoView.setVisibility(anyResults);
                m_searchResultExpandedPageInfoView.setVisibility(anyResults);
                m_footer.setVisibility(anyResults);
                break;
            case Hidden:
                m_expandControls.setVisibility(View.GONE);
                m_paginationControls.setVisibility(View.GONE);
                m_footer.setVisibility(View.GONE);
                break;
        }
    }

    private void paginationButtonVisibility(int visibility){
        for(View view : m_paginationButtons){
            view.setVisibility(visibility);
        }
    }

    private void nextPage() {
        if(m_searchResultSet.getResultCount() > RESULTS_PER_PAGE * (m_resultsPage + 1)) {
            ++m_resultsPage;
            refreshContent();
        }
    }

    private void previousPage() {
        if(m_resultsPage > 0) {
            --m_resultsPage;
            refreshContent();
        }
    }

    public void setViewState(ResultsViewState state){
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
