package com.wrld.widgets.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

class AccordionListAdapter extends BaseExpandableListAdapter {

    private AccordionDataProvider m_dataProvider;
    private int m_groupDataPosition;

    public AccordionListAdapter(AccordionDataProvider dataProvider, int groupDataPosition){
        m_dataProvider = dataProvider;
        m_groupDataPosition = groupDataPosition;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return m_dataProvider.getChildrenCount(m_groupDataPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_dataProvider.getGroup(m_groupDataPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return m_dataProvider.getChild(groupPosition, childPosition);
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
        return m_dataProvider.getGroupView(m_groupDataPosition, isExpanded, convertView, parent);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return m_dataProvider.getChildView(m_groupDataPosition, childPosition, isLastChild, convertView, parent);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
