package com.wrld.widgets.searchbox.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;

import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;

public class MenuViewController implements ExpandableListView.OnChildClickListener {

    private final MenuViewAdapter m_adapter;
    private ExpandableListView m_view;
    private SearchWidgetMenuModel m_model;

    public MenuViewController(SearchWidgetMenuModel model, ExpandableListView view) {
        m_model = model;

        m_view = view;
        m_adapter = new MenuViewAdapter(m_model, LayoutInflater.from(view.getContext()));

        m_view.setAdapter(m_adapter);
        m_view.setOnChildClickListener(this);

        updateVisibility();
    }

    private void updateVisibility() {
        m_view.setVisibility(View.GONE);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        MenuChild child = (MenuChild)m_adapter.getChild(groupPosition, childPosition);
        if (child != null) {
            child.executeCallback();
            return true;
        }
        return false;
    }

    // TODO: Add onGroupClick, onGroupCollapse, onGroupExpand
}
