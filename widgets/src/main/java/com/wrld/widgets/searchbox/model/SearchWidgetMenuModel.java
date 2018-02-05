package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

interface OnMenuOptionSelectedCallback {

    public void onMenuOptionSelected(final String text, final Object context);
}

class OnMenuOptionSelectedCallbackImpl implements OnMenuOptionSelectedCallback {
    private final OnMenuOptionSelectedCallback m_callback;

    public OnMenuOptionSelectedCallbackImpl(OnMenuOptionSelectedCallback callback) {
        m_callback = callback;
    }

    @Override
    public void onMenuOptionSelected(String text, Object context) {
        m_callback.onMenuOptionSelected(text, context);
    }
}

class MenuChild {
    private String m_text;
    private String m_icon;
    private Object m_context;
    private OnMenuOptionSelectedCallback m_callback;

    public final String getText() { return m_text; }
    public final String getIcon() { return m_icon; }
    public final Object getContext() { return m_context; }

    public MenuChild(String text, String icon, Object context, final OnMenuOptionSelectedCallback callback) {
        m_text = text;
        m_icon = icon;
        m_context = context;
        m_callback = callback;
    }

    void executeCallback() {
        if (m_callback != null) {
            m_callback.onMenuOptionSelected(m_text, m_context);
        }
    }
}

class MenuGroup {
    private String m_text;
    private Object m_context;
    private OnMenuOptionSelectedCallback m_callback;
    private List<MenuChild> m_children;

    public final String getText() { return m_text; }
    public final Object getContext() { return m_context; }
    public final List<MenuChild> getChildren() { return m_children; }

    public MenuGroup(String text, Object context, final OnMenuOptionSelectedCallback callback) {
        m_text = text;
        m_context = context;
        m_callback = callback;
        m_children = new ArrayList<MenuChild>();
    }

    public void addChild(String text, String icon, Object context, final OnMenuOptionSelectedCallback callback) {
        MenuChild child = new MenuChild(text, icon, context, callback);
        m_children.add(child);
    }

    public void addChild(MenuChild child) {
        m_children.add(child);
    }

    void executeCallback() {
        if (m_callback != null) {
            m_callback.onMenuOptionSelected(m_text, m_context);
        }
    }
}

public class SearchWidgetMenuModel {

    private List<MenuGroup> m_groups;

    public final List<MenuGroup> getGroups() { return m_groups; }

    public SearchWidgetMenuModel() {
        m_groups = new ArrayList<MenuGroup>();
    }

    public void addMenuGroup(MenuGroup group) {
        m_groups.add(group);
    }

    public void executeMenuGroupCallback(int groupIndex) {
        if (groupIndex > -1 && groupIndex < m_groups.size()) {
            MenuGroup group = m_groups.get(groupIndex);
            group.executeCallback();
        }
    }

    public void executeMenuOptionCallback(int groupIndex, int childIndex) {
        if (groupIndex > -1 && groupIndex < m_groups.size()) {
            MenuGroup group = m_groups.get(groupIndex);
            if (childIndex > -1 && childIndex < group.getChildren().size()) {
                MenuChild child = group.getChildren().get(childIndex);
                child.executeCallback();
            }
        }
    }
}
