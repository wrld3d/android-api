package com.wrld.widgets.searchbox.view;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.ISearchResult;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;

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

    private final SearchWidgetSearchModel m_searchModel;
    private final int m_previewCountPerProvider;
    private final LayoutInflater m_inflater;
    private final SearchResultFooterViewFactory m_footerViewFactory;

    public SearchResultsAdapter(SearchWidgetSearchModel searchModel,
                                LayoutInflater inflater,
                                int previewCountPerProvider) {
        m_searchModel = searchModel;
        m_previewCountPerProvider = previewCountPerProvider;
        m_inflater = inflater;
        m_footerViewFactory = new SearchResultFooterViewFactory(R.layout.search_result_group_footer);

        m_providerIdToResultSetStateMap = new HashMap<>();
        m_fullResultsShown = false;
        m_fullResultsProviderId = -1;
    }

    public void refresh() {
        List<SearchProviderQueryResult> results = m_searchModel.getCurrentQueryResults();
        for(SearchProviderQueryResult result : results) {
            if(!m_providerIdToResultSetStateMap.containsKey(result.getProviderId())) {
                ResultSetState state = ResultSetState.Preview;
                if(m_fullResultsShown) {
                    state = m_fullResultsProviderId == result.getProviderId()
                            ? ResultSetState.Full
                            : ResultSetState.Hidden;
                }
                m_providerIdToResultSetStateMap.put(result.getProviderId(), state);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int total = 0;
        if(m_searchModel.getTotalCurrentQueryResults() == 0)
        {
            return 0;
        }

        List<SearchProviderQueryResult> results = m_searchModel.getCurrentQueryResults();
        for(SearchProviderQueryResult result : results) {
            int providerId = result.getProviderId();
            total += getVisibleCountForProvider(providerId);

        }
        return total;
    }

    private int getVisibleCountForProvider(int providerId) {
        List<SearchProviderQueryResult> results = m_searchModel.getCurrentQueryResults();
        int footerChildCount = 1;

        if(!m_providerIdToResultSetStateMap.containsKey(providerId)) {
            return 0;
        }

        for(SearchProviderQueryResult result : results) {

            if(result.getProviderId() != providerId) {
                continue;
            }

            ResultSetState state = m_providerIdToResultSetStateMap.get(providerId);
            switch(state) {
                case Hidden:
                    return 0;
                case Preview:
                    if(result.getResults().length > m_previewCountPerProvider) {
                        return m_previewCountPerProvider + footerChildCount;
                    }
                    else {
                        return result.getResults().length;
                    }
                case Full:
                    return result.getResults().length + footerChildCount;
            }
        }
        return 0;
    }

    boolean isFooter(int position) {
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        SearchProviderQueryResult result = m_searchModel.getCurrentQueryResults().get(providerIndex.first);
        int providerId = result.getProviderId();

        if(!m_providerIdToResultSetStateMap.containsKey(providerId)) {
            return false;
        }

        ResultSetState state = m_providerIdToResultSetStateMap.get(providerId);
        switch(state) {
            case Hidden:
                return false;
            case Preview:
                return result.getResults().length > m_previewCountPerProvider && providerIndex.second == m_previewCountPerProvider;
            case Full:
                return position == result.getResults().length;
        }

        return false;
    }

    @Override
    public Object getItem(int position) {
        Pair<Integer,Integer> providerIndex = getProviderIndex(position);
        SearchProviderQueryResult result = m_searchModel.getCurrentQueryResults().get(providerIndex.first);
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
            Pair<Integer, Integer> providerIndex = getProviderIndex(position);
            SearchProviderQueryResult result = m_searchModel.getCurrentQueryResults().get(providerIndex.first);
            int providerId = result.getProviderId();
            // Get provider name.
            ResultSetState state = m_providerIdToResultSetStateMap.get(providerId);
            if(state == ResultSetState.Preview) {
                int hiddenResults = (result.getResults().length - getVisibleCountForProvider(providerId))-1;
                viewHolder.showExpand(hiddenResults);
            }
            else {
                viewHolder.showCollapse();
            }

        }
        else {
            ISearchResult result = (ISearchResult)getItem(position);

            String queryText = m_searchModel.getCurrentQuery() != null ? m_searchModel.getCurrentQuery().getQueryString() : "";
            ((ISearchResultViewHolder)convertView.getTag()).populate(result, queryText);
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
            int providerId = m_searchModel.getCurrentQueryResults().get(providerIndex.first).getProviderId();
            boolean isFooter = isFooter(position);
            // If 2nd == visible results length then it's the footer.
            return isFooter ? -1 : providerId;
        }
        return -1;
    }

    Pair<Integer, Integer> getProviderIndex(int position) {
        int count = 0;

        if(m_searchModel.getCurrentQueryResults() == null) {
            return null;
        }

        int setIndex = 0;
        for(SearchProviderQueryResult queryResultSet : m_searchModel.getCurrentQueryResults()){
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

        for(SearchProviderQueryResult result : m_searchModel.getCurrentQueryResults()) {
            if(m_fullResultsShown) {
                boolean isShown = m_fullResultsProviderId == result.getProviderId();
                m_providerIdToResultSetStateMap.put(result.getProviderId(), isShown ? ResultSetState.Full : ResultSetState.Hidden);
            }
            else {
                m_providerIdToResultSetStateMap.put(result.getProviderId(), ResultSetState.Preview);
            }
        }

        refresh();
    }
}
