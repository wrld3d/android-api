package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SearchResultViewHolder;

import java.util.ArrayList;

class SuggestionSearchResultController extends BaseAdapter implements SearchResultsController {

    private LayoutInflater m_inflater;

    private ListView m_container;

    private SetCollection m_sets;
    private ArrayList<SearchResultViewFactory> m_viewFactories;

    public SuggestionSearchResultController( ListView container, SetCollection resultSet) {
        m_container = container;
        m_inflater = LayoutInflater.from(container.getContext());
        m_sets = resultSet;
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

    public void setViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_viewFactories = factories;
        m_container.setAdapter(this);
    }

    @Override
    public int getCount() {
        return m_sets.getAllResultsCount();
    }

    @Override
    public Object getItem(int position) {
        return m_sets.getResultAtIndex(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return m_viewFactories.size();
    }

    @Override
    public int getItemViewType(int position){
        return m_sets.getSetForAbsolutePosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            SearchResultViewFactory viewFactory = m_viewFactories.get(getItemViewType(position));
            convertView = viewFactory.makeSearchResultView(m_inflater, parent);
            SearchResultViewHolder viewHolder = viewFactory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        SearchResult result = m_sets.getResultAtIndex(position);
        ((SearchResultViewHolder)convertView.getTag()).populate(result);

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
}
