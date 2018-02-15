package com.wrld.widgets.searchbox.view;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wrld.widgets.searchbox.model.SearchResult;
import com.wrld.widgets.searchbox.model.ObservableSearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchWidgetSuggestionModel;


public class SuggestionResultsAdapter extends BaseAdapter {

    private final ObservableSearchResultsModel m_results;
    private final SearchWidgetSuggestionModel m_suggestionModel;
    private final int m_resultsPerProvider;
    private final LayoutInflater m_inflater;

    public SuggestionResultsAdapter(ObservableSearchResultsModel results,
                                    SearchWidgetSuggestionModel suggestionModel,
                                    LayoutInflater inflater,
                                    int resultsPerProvider)
    {
        m_resultsPerProvider = resultsPerProvider;
        m_results = results;
        m_suggestionModel = suggestionModel;
        m_inflater = inflater;
    }

    @Override
    public int getCount() {
        if(m_results.getCurrentQueryResults() == null) {
            return 0;
        }

        int count = 0;
        for(SearchProviderQueryResult result : m_results.getCurrentQueryResults()) {
            count += Math.min(m_resultsPerProvider, result.getResults().length);
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        if(providerIndex != null) {
            SearchProviderQueryResult providerResult = m_results.getCurrentQueryResults().get(providerIndex.first);
            return providerResult.getResults()[providerIndex.second];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            // Model shouldn't have this, expose interface and give to something else.
            ISearchResultViewFactory viewFactory = m_suggestionModel.getViewFactoryForProvider(getItemViewType(position));

            convertView = viewFactory.makeSearchResultView(m_inflater, parent);
            ISearchResultViewHolder viewHolder = viewFactory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        SearchResult result = (SearchResult)getItem(position);
        if(result != null) {
            String queryText = m_suggestionModel.getCurrentQuery() != null ? m_suggestionModel.getCurrentQuery().getQueryString() : "";
            // TODO optional styling for first/last result - not used for suggestion default styles.
            ((ISearchResultViewHolder) convertView.getTag()).populate(result, queryText, false, false);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount(){
        // Pass in an observable repository of providers.
        return Math.max(1, m_suggestionModel.getSuggestionProviderCount());
    }

    @Override
    public int getItemViewType(int position){
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        if(providerIndex != null) {
            int providerId = m_results.getCurrentQueryResults().get(providerIndex.first).getProviderId();
            return providerId;
        }
        return -1;
    }

    private Pair<Integer, Integer> getProviderIndex(int position) {
        int count = 0;

        if(m_results.getCurrentQueryResults() == null) {
            return null;
        }

        int setIndex = 0;
        for(SearchProviderQueryResult queryResultSet : m_results.getCurrentQueryResults()){
            int actualPositionInSet = position - count;
            int setSize = Math.min(m_resultsPerProvider, queryResultSet.getResults().length);
            if(actualPositionInSet >= setSize){
                count += setSize;
                ++setIndex;
            }
            else {
                return new Pair<>(setIndex, actualPositionInSet);
            }
        }

        return null;
    }
}
