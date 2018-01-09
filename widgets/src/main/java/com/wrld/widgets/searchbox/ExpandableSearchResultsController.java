package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SearchResultViewHolder;

import java.util.ArrayList;

class ExpandableSearchResultsController extends BaseExpandableListAdapter implements SearchResultsController {

    private LayoutInflater m_inflater;
    private ExpandableListView m_container;
    private SetCollection m_sets;
    private ArrayList<SearchResultViewFactory> m_viewFactories;

    public ExpandableSearchResultsController(ExpandableListView container,
                                            SetCollection resultSets) {

        Context context = container.getContext();
        m_container = container;
        m_inflater = LayoutInflater.from(context);
        m_sets = resultSets;
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
    public int getGroupCount() {
        return m_sets.getSetCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return m_sets.getChildrenInSetCount(groupPosition);
    }

    @Override
    // TODO This sucks
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return m_sets.getResultAtIndex(groupPosition, childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = m_inflater.inflate(R.layout.searchbox_menu_group_header, parent, false);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){

        SearchResult searchResult = m_sets.getResultAtIndex(groupPosition, childPosition);
        if(convertView == null) {
            SearchResultViewFactory viewFactory = m_viewFactories.get(groupPosition);
            convertView = viewFactory.makeSearchResultView(m_inflater, searchResult, parent);
            SearchResultViewHolder viewHolder = viewFactory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);

            ((ViewGroup)convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        }

        ((SearchResultViewHolder)convertView.getTag()).populate(searchResult);

        return convertView;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition){
        return groupPosition;
    }

    @Override
    public int getChildTypeCount(){
        return m_viewFactories.size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void searchStarted() {

    }

    @Override
    public void refreshContent() {
        notifyDataSetChanged();
    }

    public void setViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_viewFactories = factories;
        m_container.setAdapter(this);
    }
}
