package com.wrld.widgets.searchbox.view;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.SearchResult;
import com.wrld.widgets.searchbox.model.ObservableSearchResultsModel;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQueryModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsAdapter extends BaseAdapter {


    public enum ResultSetState {
        Hidden,
        Preview,
        Full
    }

    private Map<Integer, ResultSetState> m_providerIdToResultSetStateMap;
    private boolean m_fullResultsShown;
    private int m_fullResultsProviderId;

    private final ObservableSearchResultsModel m_results;
    private final SearchQueryModel m_searchModel;
    private final int m_previewCountPerProvider;
    private final LayoutInflater m_inflater;
    private final SearchResultFooterViewFactory m_footerViewFactory;

    public SearchResultsAdapter(ObservableSearchResultsModel results,
                                SearchQueryModel searchModel,
                                LayoutInflater inflater,
                                int previewCountPerProvider) {
        m_results = results;
        m_searchModel = searchModel;
        m_previewCountPerProvider = previewCountPerProvider;
        m_inflater = inflater;
        m_footerViewFactory = new SearchResultFooterViewFactory(R.layout.search_result_group_footer);

        m_providerIdToResultSetStateMap = new HashMap<>();
        m_fullResultsShown = false;
        m_fullResultsProviderId = -1;
    }

    public void refresh(boolean resetState) {
        List<SearchProviderQueryResult> results = m_results.getCurrentQueryResults();

        if(resetState) {
            m_fullResultsShown = false;
        }

        if(results != null) {
            for (SearchProviderQueryResult result : results) {
                if (!m_providerIdToResultSetStateMap.containsKey(result.getProviderId()) ||
                        resetState) {
                    ResultSetState state = ResultSetState.Preview;
                    if (m_fullResultsShown) {
                        state = m_fullResultsProviderId == result.getProviderId()
                                ? ResultSetState.Full
                                : ResultSetState.Hidden;
                    }
                    m_providerIdToResultSetStateMap.put(result.getProviderId(), state);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int total = 0;
        if(m_results.getTotalCurrentQueryResults() == 0)
        {
            return 0;
        }

        List<SearchProviderQueryResult> results = m_results.getCurrentQueryResults();
        for(SearchProviderQueryResult result : results) {
            int providerId = result.getProviderId();
            total += getVisibleCountForProvider(providerId);
        }

        return total;
    }

    private int getVisibleCountForProvider(int providerId) {
        List<SearchProviderQueryResult> results = m_results.getCurrentQueryResults();
        int footerChildCount = 1;

        if(!m_providerIdToResultSetStateMap.containsKey(providerId)) {
            return 0;
        }

        for(SearchProviderQueryResult result : results) {

            if(result.getProviderId() != providerId) {
                continue;
            }

            int resultCount = result.getResults().length;

            if(resultCount == 0) {
                return 0;
            }

            ResultSetState state = m_providerIdToResultSetStateMap.get(providerId);
            switch(state) {
                case Hidden:
                    return 0;
                case Preview:
                    return resultCount > m_previewCountPerProvider
                            ? m_previewCountPerProvider + footerChildCount
                            : resultCount;

                case Full:
                    return result.getResults().length + footerChildCount;
            }
        }
        return 0;
    }

    boolean isFooter(int position) {
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        if(providerIndex == null) {
            return false;
        }

        SearchProviderQueryResult result = m_results.getCurrentQueryResults().get(providerIndex.first);
        int providerId = result.getProviderId();

        if(!m_providerIdToResultSetStateMap.containsKey(providerId)) {
            return false;
        }

        ResultSetState state = m_providerIdToResultSetStateMap.get(providerId);
        switch(state) {
            case Hidden:
                return false;
            case Preview:
                return providerIndex.second == m_previewCountPerProvider;
            case Full:
                return position == result.getResults().length;
        }

        return false;
    }

    @Override
    public Object getItem(int position) {
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        SearchProviderQueryResult result = m_results.getCurrentQueryResults().get(providerIndex.first);
        boolean isFooter = isFooter(position);
        return isFooter ? null : result.getResults()[providerIndex.second];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isFooter = isFooter(position);
        Pair<Integer, Integer> providerIndex = getProviderIndex(position);

        SearchProviderQueryResult queryResult = m_results.getCurrentQueryResults().get(providerIndex.first);
        int providerId = queryResult.getProviderId();

        if(convertView == null) {
            if(isFooter) {
                convertView = m_footerViewFactory.makeFooterView(m_inflater, parent);
                SearchResultFooterViewFactory.SearchResultFooterViewHolder viewHolder = m_footerViewFactory.makeViewHolder();
                viewHolder.initialise(convertView);
                convertView.setTag(viewHolder);
            }
            else {

                // Model shouldn't have this, expose interface and give to something else.
                ISearchResultViewFactory viewFactory = m_searchModel.getViewFactoryForProvider(getItemViewType(position));

                convertView = viewFactory.makeSearchResultView(m_inflater, parent);
                ISearchResultViewHolder viewHolder = viewFactory.makeSearchResultViewHolder();
                viewHolder.initialise(convertView);
                convertView.setTag(viewHolder);
            }
        }

        if(isFooter)
        {
            SearchResultFooterViewFactory.SearchResultFooterViewHolder viewHolder =
                    (SearchResultFooterViewFactory.SearchResultFooterViewHolder)convertView.getTag();

            ResultSetState state = m_providerIdToResultSetStateMap.get(providerId);

            if(state == ResultSetState.Preview) {
                int hiddenResults = queryResult.getResults().length - m_previewCountPerProvider;
                String providerName = m_searchModel.getProviderById(providerId).getTitle();
                viewHolder.showExpand(hiddenResults, providerName);
            }
            else {
                viewHolder.showCollapse();
            }
        }
        else {
            SearchResult result = (SearchResult)getItem(position);

            String queryText = m_searchModel.getCurrentQuery() != null ? m_searchModel.getCurrentQuery().getQueryString() : "";
            boolean firstResultInSet = providerIndex.second == 0;
            boolean lastResultInSet = providerIndex.second == getVisibleCountForProvider(providerId) - 1 ||
                    isFooter(position+1);
            ((ISearchResultViewHolder)convertView.getTag()).populate(result,
                    queryText,
                    firstResultInSet,
                    lastResultInSet);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount(){
        return Math.max(1, m_providerIdToResultSetStateMap.size()) + 1;
    }

    @Override
    public int getItemViewType(int position){
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        if(providerIndex != null) {
            int providerId = m_results.getCurrentQueryResults().get(providerIndex.first).getProviderId();
            boolean isFooter = isFooter(position);
            // If 2nd == visible results length then it's the footer.
            return isFooter ? -1 : providerId;
        }
        return -1;
    }

    Pair<Integer, Integer> getProviderIndex(int position) {
        int count = 0;

        if(m_results.getCurrentQueryResults() == null) {
            return null;
        }

        int setIndex = 0;
        for(SearchProviderQueryResult queryResultSet : m_results.getCurrentQueryResults()){
            int actualPositionInSet = position - count;
            int visibleCountInSet = getVisibleCountForProvider(queryResultSet.getProviderId());
            if(actualPositionInSet >= visibleCountInSet){
                count += visibleCountInSet;
                ++setIndex;
            }
            else {
                return new Pair<>(setIndex, actualPositionInSet);
            }
        }

        return null;
    }

    public void toggleState(int providerId) {
        m_fullResultsShown = !m_fullResultsShown;
        m_fullResultsProviderId = providerId;

        for(SearchProviderQueryResult result : m_results.getCurrentQueryResults()) {
            if(m_fullResultsShown) {
                boolean isShown = m_fullResultsProviderId == result.getProviderId();
                m_providerIdToResultSetStateMap.put(result.getProviderId(), isShown ? ResultSetState.Full : ResultSetState.Hidden);
            }
            else {
                m_providerIdToResultSetStateMap.put(result.getProviderId(), ResultSetState.Preview);
            }
        }

        refresh(false);
    }
}
