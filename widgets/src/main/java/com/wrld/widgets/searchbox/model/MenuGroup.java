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
        m_options.add(child);
    }

    public void addOption(MenuOption option) {
        m_options.add(option);
    }
}