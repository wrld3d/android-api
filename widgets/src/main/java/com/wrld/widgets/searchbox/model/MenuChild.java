package com.wrld.widgets.searchbox.model;

public class MenuChild {
    private String m_text;
    private Integer m_iconResource;
    private Object m_context;
    private OnMenuOptionSelectedCallback m_callback;

    public final String getText() { return m_text; }
    public final Integer getIconResource() { return m_iconResource; }
    public final Object getContext() { return m_context; }

    public MenuChild(String text, Integer iconResource, Object context, final OnMenuOptionSelectedCallback callback) {
        m_text = text;
        m_iconResource = iconResource;
        m_context = context;
        m_callback = callback;
    }

    public boolean executeCallback() {
        if (m_callback != null) {
            return m_callback.onMenuOptionSelected(m_text, m_context);
        }
        return false;
    }
}