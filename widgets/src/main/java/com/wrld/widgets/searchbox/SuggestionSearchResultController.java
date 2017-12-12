package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

class SuggestionSearchResultController extends BaseAdapter implements SearchResultsController {
    private LayoutInflater m_inflater;

    private SearchResultSet m_searchResultSet;
    private SearchResultViewFactory m_resultsViewFactory;

    public SuggestionSearchResultController(View container, SearchResultSet resultSet, SearchResultViewFactory viewFactory) {
        m_inflater = LayoutInflater.from(container.getContext());
        m_searchResultSet = resultSet;
        m_resultsViewFactory = viewFactory;
    }

    @Override
    public SearchResultSet.OnResultChanged getUpdateCallback(){
        return new SearchResultSet.OnResultChanged() {
            @Override
            public void invoke() {
                refreshContent();
            }
        };
    }

    @Override
    public int getCount() {
        return m_searchResultSet.getResultCount();
    }

    @Override
    public Object getItem(int position) {
        return  getResult(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    @Override
    public void refreshContent() {
        notifyDataSetChanged();
    }

    private SearchResult getResult(int position) {
        return m_searchResultSet.getResult(position);
    }
}
