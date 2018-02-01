package com.wrld.widgets.searchbox.model;

import java.util.ArrayList;
import java.util.List;

// Not quite sure how callbacks work in Java

interface OnMenuOptionSelectedCallbackInterface {

    public void onMenuOptionSelected(MenuOption option);
}

class OnMenuOptionSelectedCallbackImpl implements OnMenuOptionSelectedCallbackInterface {
    private final OnMenuOptionSelectedCallbackInterface m_callback;

    public OnMenuOptionSelectedCallbackImpl(OnMenuOptionSelectedCallbackInterface callback) {
        m_callback = callback;
    }

    @Override
    public void onMenuOptionSelected(MenuOption option) {
        m_callback.onMenuOptionSelected(option);
    }
}

// This modelling of menu options and their possible children might be a bit convoluted

class MenuOption {

    private String m_text;
    private String m_icon;
    private Object m_context;
    private OnMenuOptionSelectedCallbackInterface m_callback;

    public final String getText() { return m_text; }
    public final String getIcon() { return m_icon; }
    public final Object getContext() { return m_context; }
    public Boolean hasChildren() { return false; }

    public MenuOption(String text, String icon, Object context, final OnMenuOptionSelectedCallbackInterface callback) {
        m_text = text;
        m_icon = icon;
        m_context = context;
        m_callback = callback;
    }

    void executeCallback() {
        if (m_callback != null) {
            m_callback.onMenuOptionSelected(this);
        }
    }
}

class ExpandableMenuOption extends MenuOption {

    private List<MenuOption> m_children;

    public final List<MenuOption> getChildren() { return m_children; }

    public ExpandableMenuOption(String text, String icon) {
        super(text, icon, null, null);

        m_children = new ArrayList<MenuOption>();
    }

    public void addChild(String text, String icon, Object context, final OnMenuOptionSelectedCallbackInterface callback) {
        MenuOption option = new MenuOption(text, icon, context, callback);
        m_children.add(option);
    }

    public void addChild(MenuOption option) {
        m_children.add(option);
    }

    @Override
    public Boolean hasChildren() {
        return true;
    }
}

public class SearchWidgetMenuModel {

    private List<MenuOption> m_options;

    public final List<MenuOption> getOptions() { return m_options; }

    public SearchWidgetMenuModel() {
        m_options = new ArrayList<MenuOption>();
    }

    public void addMenuOption(MenuOption option) {
        m_options.add(option);
    }

    public void addMenuOption(ExpandableMenuOption option) {
        m_options.add(option);
    }

    public void selectMenuOption(int index) {

        MenuOption selectedMenuOption = m_options.get(index);
        if (selectedMenuOption.hasChildren()) {
            // expand menu option
        }

        selectedMenuOption.executeCallback();
    }
}
