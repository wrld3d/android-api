package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

public class SearchWidgetMenuModel {

    private List<MenuGroup> m_groups;

    public final List<MenuGroup> getGroups() { return m_groups; }

    public SearchWidgetMenuModel() {
        m_groups = new ArrayList<MenuGroup>();
    }

    public void addMenuGroup(MenuGroup group) {
        m_groups.add(group);
    }
}
