// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

class DefaultSearchResultsContainer extends BaseAdapter implements ListAdapter, SearchResultsContainer {

    private LayoutInflater m_inflater;

    private SearchResultSet m_resultsModel;
    private SearchResultSet m_suggestionsModel;
    private SearchResultViewFactory m_resultsViewFactory;
    private SearchResultViewFactory m_suggestionsViewFactory;

    private boolean m_suggestionsOn;

    public DefaultSearchResultsContainer(LayoutInflater inflator, SearchResultSet resultsModel, SearchResultViewFactory viewFactoryResults , SearchResultSet suggestionsModel , SearchResultViewFactory factorySuggestions) {

        m_inflater =inflator;

        m_resultsModel = resultsModel;
        m_suggestionsModel = suggestionsModel;
        m_suggestionsViewFactory = factorySuggestions;
        m_resultsViewFactory = viewFactoryResults;

        m_suggestionsOn = true;
    }

    @Override
    public int getCount() {
        return (m_suggestionsOn ? m_suggestionsModel.getResultCount() :m_resultsModel.getResultCount());
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
        return position;
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

        ((SearchResultViewHolder)convertView.getTag()).populate(  getResultForState(position));

        return convertView;
    }

    private SearchResult getResultForState(int position){
        return (m_suggestionsOn ? m_suggestionsModel.getResult(position) :m_resultsModel.getResult(position));
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    public void showSuggestions(boolean flag){
        if(m_suggestionsOn != flag)
        {
            m_suggestionsOn = flag;
            refresh();
        }


    }
}
