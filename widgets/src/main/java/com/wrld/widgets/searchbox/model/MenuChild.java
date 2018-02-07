package com.wrld.widgets.searchbox.model;

public class MenuChild {
    private String m_text;
    private String m_icon;
    private Object m_context;
    private IOnMenuOptionSelectedCallback m_callback;

    public final String getText() { return m_text; }
    public final String getIcon() { return m_icon; }
    public final Object getContext() { return m_context; }

    public MenuChild(String text, String icon, Object context, final IOnMenuOptionSelectedCallback callback) {
        m_text = text;
        m_icon = icon;
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