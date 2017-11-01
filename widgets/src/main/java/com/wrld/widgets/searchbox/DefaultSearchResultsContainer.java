// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.wrld.widgets.R;
import java.util.Vector;

class DefaultSearchResultsContainer extends BaseAdapter implements ListAdapter, SearchResultsContainer {

    private Vector<SearchResultViewFactory> m_viewFactories;
    private LayoutInflater m_inflater;

    private ListView m_container;

    private Vector<SearchResultViewModel> m_searchResultViewModels;

    private Vector<SearchResultSetViewModel> m_searchResultSets;

    public DefaultSearchResultsContainer(ListView container) {
        m_inflater = LayoutInflater.from(container.getContext());

        m_viewFactories = new Vector<SearchResultViewFactory>();

        m_searchResultViewModels = new Vector<SearchResultViewModel>();

        m_container = container;

        m_container.setAdapter(this);

        m_searchResultSets = new Vector<SearchResultSetViewModel>();
    }

    @Override
    public int getCount() {
        return m_searchResultViewModels.size();
    }

    @Override
    public int getViewTypeCount(){
        return m_viewFactories.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = m_searchResultViewModels.get(position).getFactoryIndex();
        return type;
    }

    @Override
    public Object getItem(int position) {
        return m_searchResultViewModels.get(position).getData();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchResultViewModel viewModel = m_searchResultViewModels.get(position);

        if(convertView == null) {
            SearchResultViewFactory factory = m_viewFactories.get(viewModel.getFactoryIndex());
            convertView = factory.makeSearchResultView(m_inflater, viewModel.getData(), parent);

            SearchResultViewHolder viewHolder = factory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        ((SearchResultViewHolder)convertView.getTag()).populate(viewModel.getData());

        return convertView;
    }

    @Override
    public void addSearchResultSet(SearchResultSet searchResultSet, SearchResultViewFactory factory) {

        int factoryIndex = 0;
        if(!m_viewFactories.contains(factory)) {
            factoryIndex = m_viewFactories.size();
            m_viewFactories.add(factory);
            // required to update number of views
            m_container.setAdapter(this);
        }
        else {
            factoryIndex = m_viewFactories.indexOf(factory);
        }

        SearchResultSetViewModel setViewModel = new SearchResultSetViewModel(searchResultSet, factoryIndex);

        m_searchResultSets.add(setViewModel);

        for(SearchResult result : searchResultSet.getAllResults()) {
            m_searchResultViewModels.add(new SearchResultViewModel(result, setViewModel));
        }
    }

    @Override
    public void refresh() {
        m_searchResultViewModels.clear();

        // TODO MOD optimise this
        for (SearchResultSetViewModel setViewModel : m_searchResultSets) {
            for (SearchResult result : setViewModel.getSearchResultSet().getAllResults()) {
                m_searchResultViewModels.add(new SearchResultViewModel(result, setViewModel));
            }
        }

        notifyDataSetChanged();
    }
}
