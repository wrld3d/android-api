package com.wrld.widgets.searchbox.view;


import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.widget.Adapter;
import android.widget.ExpandableListAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuGroup;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;

interface IMenuGroupViewHolder {
    void initialise(View view);
    void populate(MenuGroup group);
}

class MenuGroupViewFactory {

    public MenuGroupViewFactory() {
    }

    public View makeMenuGroupView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.searchbox_menu_group_header, parent, false);
    }

    private class MenuGroupViewHolder implements IMenuGroupViewHolder {
        private TextView m_title;

        public MenuGroupViewHolder(){}

        public void initialise(View view){
            m_title = (TextView) view.findViewById(R.id.searchbox_menu_item_text);
        }

        public void populate(MenuGroup group) {
            m_title.setText(group.getText());
        }
    }

    public IMenuGroupViewHolder makeMenuGroupViewHolder() {
        return new MenuGroupViewHolder();
    }
}

interface IMenuChildViewHolder {
    void initialise(View view);
    void populate(MenuChild child);
}

class MenuChildViewFactory {

    public MenuChildViewFactory() {
    }

    public View makeMenuChildView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.searchbox_menu_group_content, parent, false);
    }

    private class MenuChildViewHolder implements IMenuChildViewHolder {
        private TextView m_title;

        public MenuChildViewHolder(){}

        public void initialise(View view){
            m_title = (TextView) view.findViewById(R.id.searchbox_menu_item_text);
        }

        public void populate(MenuChild child) {
            m_title.setText(child.getText());
        }
    }

    public IMenuChildViewHolder makeMenuChildViewHolder() {
        return new MenuChildViewHolder();
    }
}

public class MenuViewAdapter implements ExpandableListAdapter {

    private final SearchWidgetMenuModel m_model;
    private final LayoutInflater m_inflater;

    private final MenuGroupViewFactory m_menuGroupViewFactory;
    private final MenuChildViewFactory m_menuChildViewFactory;

    public MenuViewAdapter(SearchWidgetMenuModel model, LayoutInflater inflater) {
        m_model = model;
        m_inflater = inflater;
        m_menuGroupViewFactory = new MenuGroupViewFactory();
        m_menuChildViewFactory = new MenuChildViewFactory();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO
    }

    @Override
    public int getGroupCount() {
        return m_model.getGroups().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition > -1 && groupPosition < m_model.getGroups().size()) {
            return m_model.getGroups().get(groupPosition).getChildren().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition > -1 && groupPosition < m_model.getGroups().size()) {
            return m_model.getGroups().get(groupPosition);
        }
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition > -1 && groupPosition < m_model.getGroups().size()) {
            MenuGroup group = m_model.getGroups().get(groupPosition);
            if (childPosition > -1 && childPosition < group.getChildren().size()) {
                return group.getChildren().get(childPosition);
            }
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TOD
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // TODO
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = m_menuGroupViewFactory.makeMenuGroupView(m_inflater, parent);
            IMenuGroupViewHolder viewHolder = m_menuGroupViewFactory.makeMenuGroupViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        MenuGroup group = (MenuGroup)getGroup(groupPosition);
        ((IMenuGroupViewHolder)convertView.getTag()).populate(group);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = m_menuChildViewFactory.makeMenuChildView(m_inflater, parent);
            IMenuChildViewHolder viewHolder = m_menuChildViewFactory.makeMenuChildViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        MenuChild child = (MenuChild)getChild(groupPosition, childPosition);
        ((IMenuChildViewHolder)convertView.getTag()).populate(child);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        // TODO
        return true;
    }

    @Override
    public boolean isEmpty() {
        // TODO
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        // TODO
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        // TODO
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        // TODO
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        // TODO
        return 0;
    }
}
