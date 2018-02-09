package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

class OnMenuOptionSelectedCallbackImpl implements OnMenuOptionSelectedCallback {
    private final OnMenuOptionSelectedCallback m_onSelectCallback;

    public OnMenuOptionSelectedCallbackImpl(OnMenuOptionSelectedCallback callback) {
        m_onSelectCallback = callback;
    }

    @Override
    public boolean onMenuOptionSelected(String text, Object context) {
        return m_onSelectCallback.onMenuOptionSelected(text, context);
    }
}

class OnMenuGroupInteractionCallbackImpl implements OnMenuGroupInteractionCallback {
    private final OnMenuGroupInteractionCallback m_onSelectCallback;

    public OnMenuGroupInteractionCallbackImpl(OnMenuGroupInteractionCallback callback) {
        m_onSelectCallback = callback;
    }

    @Override
    public void onMenuGroupInteraction(String text, Object context) {
        m_onSelectCallback.onMenuGroupInteraction(text, context);
    }
}

public class MenuGroup {
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

    public MenuGroup(String text, Object context, final OnMenuOptionSelectedCallback callback) {
        m_text = text;
        m_context = context;
        m_onSelectCallback = callback;
        m_children = new ArrayList<MenuChild>();
    }

    public void addChild(String text, String icon, Object context, final OnMenuOptionSelectedCallback callback) {
        MenuChild child = new MenuChild(text, icon, context, callback);
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