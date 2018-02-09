package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

public class SearchWidgetMenuModel {

    private List<MenuGroup> m_groups;
    private OnMenuChangedListener m_listener;

    public final List<MenuGroup> getGroups() { return m_groups; }

    public void setListener(OnMenuChangedListener listener)
    {
        m_listener = listener;
    }

    public SearchWidgetMenuModel() {
        m_groups = new ArrayList<MenuGroup>();
        m_listener = null;
    }

    public void addMenuGroup(MenuGroup group) {
        m_groups.add(group);
        if (m_listener != null) {
            m_listener.onMenuChanged();
        }
    }
}
