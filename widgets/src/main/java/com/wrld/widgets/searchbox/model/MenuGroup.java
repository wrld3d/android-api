package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

class OnMenuOptionSelectedCallbackImpl implements IOnMenuOptionSelectedCallback {
    private final IOnMenuOptionSelectedCallback m_callback;

    public OnMenuOptionSelectedCallbackImpl(IOnMenuOptionSelectedCallback callback) {
        m_callback = callback;
    }

    @Override
    public void onMenuOptionSelected(String text, Object context) {
        m_callback.onMenuOptionSelected(text, context);
    }
}

public class MenuGroup {
    private String m_text;
    private Object m_context;
    private IOnMenuOptionSelectedCallback m_callback;
    private List<MenuChild> m_children;

    public final String getText() { return m_text; }
    public final Object getContext() { return m_context; }
    public final List<MenuChild> getChildren() { return m_children; }

    public MenuGroup(String text, Object context, final IOnMenuOptionSelectedCallback callback) {
        m_text = text;
        m_context = context;
        m_callback = callback;
        m_children = new ArrayList<MenuChild>();
    }

    public void addChild(String text, String icon, Object context, final IOnMenuOptionSelectedCallback callback) {
        MenuChild child = new MenuChild(text, icon, context, callback);
        m_children.add(child);
    }

    public void addChild(MenuChild child) {
        m_children.add(child);
    }

    public void executeCallback() {
        if (m_callback != null) {
            m_callback.onMenuOptionSelected(m_text, m_context);
        }
    }
}