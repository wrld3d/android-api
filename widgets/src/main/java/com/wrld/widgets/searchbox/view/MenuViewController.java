package com.wrld.widgets.searchbox.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuGroup;
import com.wrld.widgets.searchbox.model.MenuOption;
import com.wrld.widgets.searchbox.model.MenuChangedListener;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;

public class MenuViewController implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener,
        MenuChangedListener {

    private ImageButton m_openMenuButtonView;
    private View m_menuContainerView;
    private MenuViewObserver m_menuViewObserver;
    private final MenuViewAdapter m_expandableListAdapter;
    private ExpandableListView m_expandableListView;
    private SearchWidgetMenuModel m_model;
    private boolean m_isOpen;
    private int m_previousGroup;

    public MenuViewController(SearchWidgetMenuModel model,
                              View view,
                              ImageButton openMenuButtonView,
                              MenuViewObserver menuViewObserver) {
        m_model = model;

        m_model.setListener(this);

        m_menuContainerView = view;
        m_menuViewObserver = menuViewObserver;

        m_expandableListView = (ExpandableListView)m_menuContainerView.findViewById(R.id.searchbox_menu_groups);;
        m_expandableListAdapter = new MenuViewAdapter(m_model, LayoutInflater.from(m_expandableListView.getContext()));

        m_expandableListView.setAdapter(m_expandableListAdapter);
        m_expandableListView.setOnChildClickListener(this);
        m_expandableListView.setOnGroupClickListener(this);
        m_expandableListView.setOnGroupExpandListener(this);
        m_expandableListView.setOnGroupCollapseListener(this);

        m_openMenuButtonView = openMenuButtonView;
        m_openMenuButtonView.setOnClickListener(new View.OnClickListener() {
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
        m_previousGroup = -1;

        updateVisibility();
        updateMenuButtonVisibility();
        updateMenuTitle();
    }

    public void open() {
        if (!hasMenu()) { return; }
        m_isOpen = true;
        m_menuViewObserver.onOpened();
        updateVisibility();
    }

    public void close() {
        m_isOpen = false;
        m_menuViewObserver.onClosed();
        updateVisibility();
    }

    public boolean isMenuOpen() { return m_isOpen; }

    private boolean hasMenu() {
        return !m_model.getGroups().isEmpty();
    }

    private void updateVisibility() {
        if (m_isOpen) {
            m_menuContainerView.setVisibility(View.VISIBLE);
        }
        else {
            m_menuContainerView.setVisibility(View.GONE);
        }
    }

    private void updateMenuButtonVisibility() {
        if (hasMenu()) {
            m_openMenuButtonView.setVisibility(View.VISIBLE);
        }
        else {
            m_openMenuButtonView.setVisibility(View.GONE);
        }
    }

    private void updateMenuTitle() {
        ((TextView)m_menuContainerView.findViewById(R.id.searchbox_menu_title)).setText(m_model.getTitle());
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        MenuChild child = (MenuChild)m_expandableListAdapter.getChild(groupPosition, childPosition);
        if (child != null) {
            boolean closeMenu = child.executeCallback();
            m_menuViewObserver.onChildSelected(child);
            if (closeMenu) {
                close();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        Object expandableListViewGroup = m_expandableListAdapter.getGroup(groupPosition);
        if (MenuOption.class.isInstance(expandableListViewGroup)) {
            MenuOption menuOption = (MenuOption)expandableListViewGroup;
            if (!menuOption.hasChildren()) {
                boolean closeMenu = menuOption.executeOnSelectCallback();
                m_menuViewObserver.onOptionSelected(menuOption);
                if (closeMenu) {
                    close();
                }

                return true;
            }
        }
        if (MenuGroup.class.isInstance(expandableListViewGroup)) {
            return true;
        }
        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        if (groupPosition != m_previousGroup) {
            m_expandableListView.collapseGroup(m_previousGroup);
        }
        m_previousGroup = groupPosition;

        Object expandableListViewGroup = m_expandableListAdapter.getGroup(groupPosition);
        if (MenuOption.class.isInstance(expandableListViewGroup)) {
            ((MenuOption)expandableListViewGroup).executeOnExpandCallback();
            m_menuViewObserver.onOptionExpanded((MenuOption)expandableListViewGroup);
        }
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        Object expandableListViewGroup = m_expandableListAdapter.getGroup(groupPosition);
        if (MenuOption.class.isInstance(expandableListViewGroup)) {
            ((MenuOption)expandableListViewGroup).executeOnCollapseCallback();
            m_menuViewObserver.onOptionCollapsed((MenuOption)expandableListViewGroup);
        }
    }

    @Override
    public void onMenuChanged() {
        ((BaseAdapter) m_expandableListView.getAdapter()).notifyDataSetChanged();
        updateMenuButtonVisibility();
    }

    @Override
    public void onMenuTitleChanged() {
        updateMenuTitle();
    }
}
