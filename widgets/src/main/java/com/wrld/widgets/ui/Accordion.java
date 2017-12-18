package com.wrld.widgets.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.wrld.widgets.R;

import java.util.ArrayList;

public class Accordion {

    private class AccordionExpandableListView{

        private ExpandableListView m_listView;

        LinearLayout.LayoutParams m_layoutParams;
        public ExpandableListView getExpandableListView() { return m_listView;}

        public AccordionExpandableListView(View container){
            m_listView = (ExpandableListView)container.findViewById(R.id.accordion_group);
            m_layoutParams = (LinearLayout.LayoutParams)m_listView.getLayoutParams();
        }

        public void expand(){
            m_layoutParams.weight = 1.0f;
            m_listView.expandGroup(0);
        }

        public void collapse(){
            m_layoutParams.weight = 0.0f;
            m_listView.collapseGroup(0);
        }
    }

    private ViewGroup m_root;
    private AccordionDataProvider m_dataProvider;
    private LayoutInflater m_inflater;

    private int m_groupLayoutResourceId;

    private ArrayList<AccordionExpandableListView> m_expandableListViews;

    private boolean m_isExpanded;
    private int m_expandedGroupIndex;

    private boolean m_collapseOtherViewsOnExpand = true;

    public Accordion(ViewGroup container, AccordionDataProvider dataProvider, int groupLayoutResourceId){
        m_root = container;
        m_inflater = LayoutInflater.from(container.getContext());
        m_dataProvider = dataProvider;
        m_groupLayoutResourceId = groupLayoutResourceId;

        m_expandableListViews = new ArrayList<AccordionExpandableListView>();
        m_isExpanded = false;

        dataProvider.registerGroupAddedCallback(new AccordionDataProvider.OnGroupAddedCallback() {
            @Override
            public void invoke() {
                addGroup();
            }
        });
    }

    public void collapseOtherViewsOnExpand(boolean collapseOthers){
        m_collapseOtherViewsOnExpand = collapseOthers;
    }

    public void clear(){
        m_root.removeAllViews();
    }

    public void addGroups(int count){
        for(int i = 0; i < count; ++i){
            addGroup();
        }
    }

    public void addGroup(){
        View newGroup = m_inflater.inflate(m_groupLayoutResourceId, m_root, false);
        int groupIndex = m_expandableListViews.size();
        m_root.addView(newGroup, groupIndex);
        AccordionExpandableListView viewSet = new AccordionExpandableListView(newGroup);
        m_expandableListViews.add(viewSet);
        ExpandableListView listView = viewSet.getExpandableListView();
        listView.setAdapter(new AccordionListAdapter(m_dataProvider, groupIndex));
        listView.setOnChildClickListener(onChildClicked(groupIndex));
        listView.setOnGroupClickListener(onGroupClicked(groupIndex));
    }

    public void expandGroup(int groupIndex){
        AccordionExpandableListView listView = m_expandableListViews.get(groupIndex);
        if(m_isExpanded && m_collapseOtherViewsOnExpand){
            m_expandableListViews.get(m_expandedGroupIndex).collapse();
        }
        listView.expand();
        m_expandedGroupIndex = groupIndex;
        m_isExpanded = true;
    }

    public void collapseGroup(int groupIndex){
        if(groupIndex == m_expandedGroupIndex) {
            m_expandableListViews.get(groupIndex).collapse();
            m_isExpanded = false;
        }
    }

    private ExpandableListView.OnChildClickListener onChildClicked(final int groupIndex){
        return new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                m_dataProvider.getChild(groupIndex, childPosition).clicked();
                return true;
            }
        };
    }

    private ExpandableListView.OnGroupClickListener onGroupClicked(final int groupIndex){
        return new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(!m_isExpanded || m_expandedGroupIndex != groupIndex){
                    expandGroup(groupIndex);
                }
                else if(m_isExpanded && m_expandedGroupIndex == groupIndex){
                    collapseGroup(groupIndex);
                }
                m_dataProvider.getGroup(groupIndex).clicked();
                return true;
            }
        };
    }
}
