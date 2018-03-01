package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

public class MenuOption {
    private String m_text;
    private Object m_context;
    private OnMenuOptionSelectedCallback m_onSelectCallback;
    private OnMenuGroupInteractionCallback m_onExpandCallback;
    private OnMenuGroupInteractionCallback m_onCollapseCallback;
    private List<MenuChild> m_children;

    public final String getText() { return m_text; }
    public final Object getContext() { return m_context; }
    public final List<MenuChild> getChildren() { return m_children; }
    public final boolean hasChildren() { return !m_children.isEmpty(); }

    public MenuOption(String text) {
        this(text, null, null, null, null);
    }

    public MenuOption(String text, Object context, final OnMenuOptionSelectedCallback onSelectCallback) {
        this(text, context, onSelectCallback, null, null);
    }

    public MenuOption(String text, Object context, final OnMenuGroupInteractionCallback onExpandCallback, final OnMenuGroupInteractionCallback onCollapseCallback) {
        this(text, context, null, onExpandCallback, onCollapseCallback);
    }

    private MenuOption(String text, Object context, final OnMenuOptionSelectedCallback onSelectCallback, final OnMenuGroupInteractionCallback onExpandCallback, final OnMenuGroupInteractionCallback onCollapseCallback) {
        m_text = text;
        m_context = context;
        m_onSelectCallback = onSelectCallback;
        m_onExpandCallback = onExpandCallback;
        m_onCollapseCallback = onCollapseCallback;
        m_children = new ArrayList<MenuChild>();
    }

    public void addChild(String text, Integer iconResource, Object context, final OnMenuOptionSelectedCallback callback) {
        MenuChild child = new MenuChild(text, iconResource, context, callback);
        m_children.add(child);
    }

    public void addChild(MenuChild child) {
        m_children.add(child);
    }

    public boolean executeOnSelectCallback() {
        if (m_onSelectCallback != null) {
            return m_onSelectCallback.onMenuOptionSelected(m_text, m_context);
        }
        return false;
    }

    public void executeOnExpandCallback() {
        if (m_onExpandCallback != null) {
            m_onExpandCallback.onMenuGroupInteraction(m_text, m_context);
        }
    }

    public void executeOnCollapseCallback() {
        if (m_onCollapseCallback != null) {
            m_onCollapseCallback.onMenuGroupInteraction(m_text, m_context);
        }
    }
}