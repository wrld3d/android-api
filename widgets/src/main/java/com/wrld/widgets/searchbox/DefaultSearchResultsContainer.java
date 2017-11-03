// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.wrld.widgets.R;

class DefaultSearchResultsContainer extends BaseAdapter implements ListAdapter, SearchResultsContainer {

    private LayoutInflater m_inflater;

    private SearchResultSet m_resultsModel;
    private SearchResultSet m_suggestionsModel;
    private SearchResultViewFactory m_resultsViewFactory;
    private SearchResultViewFactory m_suggestionsViewFactory;

    private boolean m_suggestionsOn;

    private final int RESULTS_PER_PAGE = 10;

    private int m_resultsPage;

    private TextView m_searchResultInfo;
    private View m_setContainer;
    private View m_expandControls;
    private View m_paginationControls;
    private View m_resultsView;

    private final String RESULTS_INFO = "%d-%d of %d";

    public DefaultSearchResultsContainer(LayoutInflater inflator, View container,
                                         SearchResultSet resultsModel, SearchResultViewFactory viewFactoryResults ,
                                         SearchResultSet suggestionsModel , SearchResultViewFactory factorySuggestions) {

        m_setContainer = container;
        m_searchResultInfo = (TextView) container.findViewById(R.id.search_pagination_results_info);
        m_expandControls = container.findViewById(R.id.expand_controls);
        m_paginationControls = container.findViewById(R.id.pagination_controls);

        m_resultsView = container.findViewById(R.id.search_result_list);
        m_inflater =inflator;

        m_resultsModel = resultsModel;
        m_suggestionsModel = suggestionsModel;
        m_suggestionsViewFactory = factorySuggestions;
        m_resultsViewFactory = viewFactoryResults;

        m_suggestionsOn = true;
    }

    @Override
    public int getCount() {
        if(m_suggestionsOn){
            return m_suggestionsModel.getResultCount();
        }

        return Math.min(m_resultsModel.getResultCount(), RESULTS_PER_PAGE);
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return m_suggestionsOn ? 1 : 0;
    }

    @Override
    public Object getItem(int position) {
        return  getResultForState(position);
    }

    @Override
    public long getItemId(int position) {
        if(m_suggestionsOn) {
            return position;
        }

        int actualResultPosition = position + m_resultsPage * RESULTS_PER_PAGE;
        return actualResultPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

            SearchResultViewFactory factory = (m_suggestionsOn ? m_suggestionsViewFactory : m_resultsViewFactory);

            convertView = factory.makeSearchResultView(m_inflater, getResultForState(position), parent);

            SearchResultViewHolder viewHolder = factory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        ((SearchResultViewHolder)convertView.getTag()).populate(getResultForState(position));

        return convertView;
    }

    private SearchResult getResultForState(int position) {
        if(m_suggestionsOn) {
            return m_suggestionsModel.getResult(position);
        }

        int actualResultPosition = position + m_resultsPage * RESULTS_PER_PAGE;
        return m_resultsModel.getResult(actualResultPosition);
    }

    @Override
    public void refresh() {
        m_resultsPage = 0;
        updateSearchResults();
    }

    public void showSuggestions(boolean flag) {
        if(m_suggestionsOn != flag) {
            m_suggestionsOn = flag;
            refresh();
        }

        int displayControls = m_suggestionsOn ? View.GONE : View.VISIBLE;

        m_expandControls.setVisibility(displayControls);
        m_paginationControls.setVisibility(View.GONE);
    }

    public void nextPage() {
        if(m_resultsModel.getResultCount() > RESULTS_PER_PAGE * (m_resultsPage + 1)) {
            ++m_resultsPage;
            updateSearchResults();
        }
    }

    public void previousPage() {
        if(m_resultsPage > 0) {
            --m_resultsPage;
            updateSearchResults();
        }
    }

    private void updateSearchResults() {
        notifyDataSetChanged();
        int resultsStart = m_resultsPage * RESULTS_PER_PAGE;
        m_searchResultInfo.setText(String.format(RESULTS_INFO, resultsStart, resultsStart + RESULTS_PER_PAGE, m_resultsModel.getResultCount()));
    }

    public void setState(int state){

        LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) m_setContainer.getLayoutParams();

        if(state == 0){
            m_expandControls.setVisibility(View.VISIBLE);
            m_paginationControls.setVisibility(View.GONE);
            m_resultsView.setVisibility(View.GONE);
            createAnimation(loparams.weight, 1f);
        }
        if(state == 1){
            m_expandControls.setVisibility(View.VISIBLE);
            m_paginationControls.setVisibility(View.GONE);
            m_resultsView.setVisibility(View.VISIBLE);
            createAnimation(loparams.weight, 1f);
        }
        if(state == 2){
            m_expandControls.setVisibility(View.GONE);
            m_paginationControls.setVisibility(View.VISIBLE);
            m_resultsView.setVisibility(View.VISIBLE);
            createAnimation(loparams.weight, 0.15f);
        }
    }

    private void createAnimation(float startWeight, float deltaWeight)
    {
        ExpandAnimation anim = new ExpandAnimation(m_setContainer, startWeight, deltaWeight);
        anim.setDuration(500);
        m_setContainer.startAnimation(anim);
    }



}
