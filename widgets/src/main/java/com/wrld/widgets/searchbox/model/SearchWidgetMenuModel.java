package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

public class SearchWidgetMenuModel {

    private String m_title;
    private List<MenuGroup> m_groups;
    private MenuChangedListener m_listener;

    public final String getTitle() { return m_title; }
    public final List<MenuGroup> getGroups() { return m_groups; }

    public void setListener(MenuChangedListener listener)
    {
        m_listener = listener;
    }

    public SearchWidgetMenuModel() {
        m_title = "Menu";
        m_groups = new ArrayList<MenuGroup>();
        m_listener = null;
    }

    public void setTitle(String title) {
        m_title = title;
        if(m_listener != null) {
            m_listener.onMenuTitleChanged();
        }
    }

    public void addMenuGroup(MenuGroup group) {
        if(!m_groups.contains(group)) {
            group.setChangedListener(m_listener);
            m_groups.add(group);
            if (m_listener != null) {
                m_listener.onMenuChanged();
            }
        }
    }

    public MenuGroup getGroupAt(int index) {
        return m_groups.get(index);
    }

    public void removeMenuGroup(MenuGroup group) {
        if(m_groups.contains(group)) {
            group.setChangedListener(null);
            m_groups.remove(group);
            if(m_listener != null) {
                m_listener.onMenuChanged();
            }
        }
    }

    public void clearMenu() {
        for(MenuGroup group : m_groups) {
            group.setChangedListener(null);
        }
        m_groups.clear();
        if(m_listener != null) {
            m_listener.onMenuChanged();
        }
    }
}
