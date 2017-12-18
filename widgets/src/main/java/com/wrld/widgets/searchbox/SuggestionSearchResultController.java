package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wrld.widgets.R;

class SuggestionSearchResultController extends BaseAdapter implements SearchResultsController {
    private LayoutInflater m_inflater;

    private SearchResultSet m_searchResultSet;
    private SearchQueryHandler m_searchQueryHandler;
    private SearchResultViewFactory m_resultsViewFactory;
    private View m_container;
    private TextView m_titleView;
    private String m_titleFormatText;

    private int m_maxSuggestions = 4;

    public SuggestionSearchResultController(String titleFormatText, View container, SearchResultSet resultSet, SearchResultViewFactory viewFactory) {
        m_container = container;
        m_inflater = LayoutInflater.from(container.getContext());
        m_searchResultSet = resultSet;
        m_resultsViewFactory = viewFactory;
        m_container.setVisibility(View.GONE);

        m_titleFormatText = titleFormatText;
        m_titleView = (TextView)container.findViewById(R.id.search_set_title);
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
        return Math.min(m_maxSuggestions, m_searchResultSet.getResultCount());
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
        int containerVisibility = getCount() > 0 ? View.VISIBLE: View.GONE;
        if(containerVisibility != m_container.getVisibility()) {
            m_container.setVisibility(containerVisibility);
        }

        notifyDataSetChanged();
    }

    public void updateTitle(String queryText){
        m_titleView.setText(String.format(m_titleFormatText, queryText));
    }

    public void hide(){
        m_container.setVisibility(View.GONE);
    }

    private SearchResult getResult(int position) {
        return m_searchResultSet.getResult(position);
    }
}
