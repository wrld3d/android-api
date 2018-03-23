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
    private String m_title;
    private boolean m_hasTitle;
    private List<MenuOption> m_options;
    private MenuChangedListener m_listener;

    public final String getTitle() { return m_title; }
    public final boolean hasTitle() { return m_hasTitle; }
    public final List<MenuOption> getOptions() { return m_options; }

    public MenuGroup() {
        m_title = "";
        m_hasTitle = false;
        m_options = new ArrayList<MenuOption>();
    }

    public MenuGroup(String title) {
        m_title = title;
        m_hasTitle = true;
        m_options = new ArrayList<MenuOption>();
    }

    public void addOption(String text, Object context, final OnMenuOptionSelectedCallback callback) {
        MenuOption child = new MenuOption(text, context, callback);
        addOption(child);
    }

    public void addOption(MenuOption option) {
        if(!m_options.contains(option)) {
            option.setChangedListener(m_listener);
            m_options.add(option);
            if (m_listener != null) {
                m_listener.onMenuChanged();
            }
        }
    }

    public MenuOption getOptionAt(int index) {
        return m_options.get(index);
    }

    public void removeOption(MenuOption option) {
        if(m_options.contains(option)) {
            option.setChangedListener(null);
            m_options.remove(option);
            if (m_listener != null) {
                m_listener.onMenuChanged();
            }
        }
    }

    public void removeAllOptions() {
        for(MenuOption option : m_options) {
            option.setChangedListener(null);
        }
        m_options.clear();
        if(m_listener != null) {
            m_listener.onMenuChanged();
        }
    }

    void setChangedListener(MenuChangedListener listener) {

        m_listener = listener;
        for(MenuOption option : m_options) {
            option.setChangedListener(listener);
        }
    }

}