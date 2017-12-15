package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wrld.widgets.R;
import com.wrld.widgets.ui.AccordionDataProvider;
import com.wrld.widgets.ui.AccordionItem;

import java.util.ArrayList;

class SearchMenuContent implements AccordionDataProvider {

    private ArrayList<SearchBoxMenuGroup> m_groups;

    LayoutInflater m_inflater;

    private ArrayList<OnGroupAddedCallback> m_groupAddedCallbacks;

    public SearchMenuContent(LayoutInflater inflater){
        m_inflater = inflater;
        m_groups = new ArrayList<SearchBoxMenuGroup>();
        m_groupAddedCallbacks = new ArrayList<OnGroupAddedCallback>();
    }

    public SearchBoxMenuGroup addGroup(String title){
        SearchBoxMenuGroup group = new SearchBoxMenuGroup(title);
        m_groups.add(group);

        for(OnGroupAddedCallback callback : m_groupAddedCallbacks){
            callback.invoke();
        }

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
    public AccordionItem getGroup(int groupPosition) {
        return getSearchBoxMenuGroup(groupPosition);
    }

    public SearchBoxMenuGroup getSearchBoxMenuGroup(int groupPosition) {
        return m_groups.get(groupPosition);
    }

    @Override
    public AccordionItem getChild(int groupPosition, int childPosition) {
        return m_groups.get(groupPosition).get(childPosition);
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
    public void registerGroupAddedCallback(OnGroupAddedCallback groupAddedCallback){
        m_groupAddedCallbacks.add(groupAddedCallback);
    }

    @Override
    public void deregisterGroupAddedCallback(OnGroupAddedCallback groupAddedCallback){
        m_groupAddedCallbacks.remove(groupAddedCallback);
    }
}
