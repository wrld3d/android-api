package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private class GroupHeaderViewHolder {

        private View m_progressBar;

        public GroupHeaderViewHolder(View view) {
            m_progressBar = view.findViewById(R.id.searchbox_set_search_in_progress_view);
        }

        public void showProgressBar(boolean isVisible){
            m_progressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }


    private class GroupFooterViewHolder {

        private TextView m_text;

        public GroupFooterViewHolder(View view) {
            m_text = (TextView)view.findViewById(R.id.searchbox_set_expand_info);
        }

        public void setText(String text){
            m_text.setText(text);
        }
    }

    public ExpandableSearchResultsController(ExpandableListView container,
                                            SetCollection resultSets) {

        Context context = container.getContext();
        m_container = container;
        m_inflater = LayoutInflater.from(context);
        m_sets = resultSets;
        m_sets.addOnResultChangedHandler(new SetCollection.OnResultChanged() {
            @Override
            public void invoke(int setCompleted) {
                m_container.expandGroup(setCompleted);
            }
        });
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
        return m_sets.getChildrenInSetCount(groupPosition) + 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_sets.getSet(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        int childType = getChildType(groupPosition, childPosition);
        if(childType == getChildTypeCount()){
            return null;
        }
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
            convertView = m_inflater.inflate(R.layout.search_result_group_header, parent, false);
            GroupHeaderViewHolder viewHolder = new GroupHeaderViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ((GroupHeaderViewHolder)convertView.getTag()).showProgressBar(!isExpanded);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){

        if(convertView == null) {
            if(isLastChild){
                convertView = m_inflater.inflate(R.layout.search_result_group_footer, parent, false);
                GroupFooterViewHolder footerViewHolder = new GroupFooterViewHolder(convertView);
                convertView.setTag(footerViewHolder);
            }
            else{
                SearchResultViewFactory viewFactory = m_viewFactories.get(groupPosition);
                convertView = viewFactory.makeSearchResultView(m_inflater, parent);
                SearchResultViewHolder viewHolder = viewFactory.makeSearchResultViewHolder();
                viewHolder.initialise(convertView);
                convertView.setTag(viewHolder);

                ((ViewGroup)convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }
        }

        if(!isLastChild) {
            SearchResult searchResult = m_sets.getResultAtIndex(groupPosition, childPosition);
            ((SearchResultViewHolder) convertView.getTag()).populate(searchResult);
        }
        else{
            SearchResultSet set = m_sets.getSet(groupPosition);
            GroupFooterViewHolder footerViewHolder = ((GroupFooterViewHolder) convertView.getTag());
            footerViewHolder.setText("See More results (" + set.getResultCount() + ") results");
        }

        return convertView;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition){
        int childrenInSet = m_sets.getChildrenInSetCount(groupPosition);
        if(childPosition == childrenInSet){
            return m_viewFactories.size();
        }
        return groupPosition;
    }

    @Override
    public int getChildTypeCount(){
        return m_viewFactories.size() + 1;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void searchStarted() {
        expandGroups(false);
    }

    @Override
    public void refreshContent() {
        notifyDataSetChanged();
    }

    public void setViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_viewFactories = factories;
        m_container.setAdapter(this);
    }

    private void expandGroups(boolean isExpanded){
        for(int i = 0; i < getGroupCount(); ++i){
            if(isExpanded){
                m_container.expandGroup(i);
            }
            else {
                m_container.collapseGroup(i);
            }
        }
    }
}
