package com.wrld.widgets.searchbox.view;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wrld.widgets.searchbox.model.ISearchResult;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;


public class SuggestionResultsAdapter extends BaseAdapter {

    private final SearchWidgetSearchModel m_model;
    private final int m_resultsPerProvider;
    private final LayoutInflater m_inflater;

    public SuggestionResultsAdapter(SearchWidgetSearchModel model, LayoutInflater inflater, int resultsPerProvider)
    {
        m_resultsPerProvider = resultsPerProvider;
        m_model = model;
        m_inflater = inflater;
    }

    @Override
    public int getCount() {
        int max = m_model.getSuggestionProviderCount() * m_resultsPerProvider;
        int count = Math.min(max, m_model.getTotalCurrentQueryResults());
        return count;
    }

    @Override
    public Object getItem(int position) {
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        if(providerIndex != null) {
            SearchProviderQueryResult providerResult = m_model.getCurrentQueryResults().get(providerIndex.first);
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
            ISearchResultViewFactory viewFactory = m_model.getViewFactoryForProvider(getItemViewType(position));

            convertView = viewFactory.makeSearchResultView(m_inflater, parent);
            ISearchResultViewHolder viewHolder = viewFactory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        ISearchResult result = (ISearchResult)getItem(position);

        String queryText = m_model.getCurrentQuery() != null ? m_model.getCurrentQuery().getQueryString() : "";
        ((ISearchResultViewHolder)convertView.getTag()).populate(result, queryText);

        return convertView;
    }

    @Override
    public int getViewTypeCount(){
        // Pass in an observable repository of providers.
        return Math.max(1, m_model.getSuggestionProviderCount());
    }

    @Override
    public int getItemViewType(int position){
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        if(providerIndex != null) {
            int providerId = m_model.getCurrentQueryResults().get(providerIndex.first).getProviderId();
            return providerId;
        }
        return -1;
    }

    private Pair<Integer, Integer> getProviderIndex(int position) {
        int count = 0;

        if(m_model.getCurrentQueryResults() == null) {
            return null;
        }

        int setIndex = 0;
        for(SearchProviderQueryResult queryResultSet : m_model.getCurrentQueryResults()){
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
