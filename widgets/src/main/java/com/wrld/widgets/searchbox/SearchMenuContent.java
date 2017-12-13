package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.wrld.widgets.R;

import java.util.ArrayList;

class SearchMenuContent extends BaseExpandableListAdapter {

    private ArrayList<SearchBoxMenuGroup> m_groups;

    LayoutInflater m_inflater;

    public SearchMenuContent(LayoutInflater inflater){
        m_inflater = inflater;
        m_groups = new ArrayList<SearchBoxMenuGroup>();
    }

    public SearchBoxMenuGroup addGroup(String title){
        SearchBoxMenuGroup group = new SearchBoxMenuGroup(title);
        m_groups.add(group);
        return group;
    }

    @Override
    public int getGroupCount() {
        return m_groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return m_groups.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getSearchBoxMenuGroup(groupPosition);
    }

    public SearchBoxMenuGroup getSearchBoxMenuGroup(int index) {
        return m_groups.get(index);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return m_groups.get(groupPosition).get(childPosition);
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = m_inflater.inflate(R.layout.searchbox_menu_group_header, parent, false);
            SearchBoxMenuGroupViewHolder viewHolder = new SearchBoxMenuGroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        int icon = android.R.color.transparent;
        SearchBoxMenuGroup group = m_groups.get(groupPosition);
        if(group.size() > 0){
            icon = isExpanded ? R.drawable.expander_open:R.drawable.expander;
        }
        ((SearchBoxMenuGroupViewHolder)convertView.getTag()).populate(group.getTitle(), icon);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = m_inflater.inflate(R.layout.searchbox_menu_group_content, parent, false);
            SearchBoxMenuChildViewHolder viewHolder = new SearchBoxMenuChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ((SearchBoxMenuChildViewHolder)convertView.getTag()).populate(m_groups.get(groupPosition).get(childPosition).getTitle());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
