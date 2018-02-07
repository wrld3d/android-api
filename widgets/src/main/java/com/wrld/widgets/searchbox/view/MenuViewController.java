package com.wrld.widgets.searchbox.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;

public class MenuViewController implements ExpandableListView.OnChildClickListener {

    private View m_menuContainerView;
    private final MenuViewAdapter m_expandableListAdapter;
    private ExpandableListView m_expandableListView;
    private SearchWidgetMenuModel m_model;
    private boolean m_isOpen;

    public MenuViewController(SearchWidgetMenuModel model, View view, ImageButton openMenuButtonView) {
        m_model = model;

        m_menuContainerView = view;

        m_expandableListView = (ExpandableListView)m_menuContainerView.findViewById(R.id.searchbox_menu_groups);;
        m_expandableListAdapter = new MenuViewAdapter(m_model, LayoutInflater.from(m_expandableListView.getContext()));

        m_expandableListView.setAdapter(m_expandableListAdapter);
        m_expandableListView.setOnChildClickListener(this);

        openMenuButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                open();
            }
        });

        ImageButton backButtonView = (ImageButton)m_menuContainerView.findViewById(R.id.searchbox_menu_back);
        backButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                close();
            }
        });

        m_isOpen = false;

        updateVisibility();
    }

    public void open() {
        m_isOpen = true;
        updateVisibility();
    }

    public void close() {
        m_isOpen = false;
        updateVisibility();
    }

    private void updateVisibility() {
        if (m_isOpen) {
            m_menuContainerView.setVisibility(View.VISIBLE);
        }
        else {
            m_menuContainerView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        MenuChild child = (MenuChild)m_expandableListAdapter.getChild(groupPosition, childPosition);
        if (child != null) {
            boolean closeMenu = child.executeCallback();
            if (closeMenu) {
                close();
            }
            return true;
        }
        return false;
    }

    // TODO: Add onGroupClick, onGroupCollapse, onGroupExpand
}
