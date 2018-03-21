package com.wrld.widgets.searchbox.view;


import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuOption;

import java.util.ArrayList;
import java.util.List;

public class MenuViewObserver implements MenuViewListener {

    private List<MenuViewListener> m_listeners;

    public MenuViewObserver() {
        m_listeners = new ArrayList<>();
    }

    void addMenuListener(MenuViewListener listener) {
        if(listener != null) {
            m_listeners.add(listener);
        }
    }

    void removeMenuListener(MenuViewListener listener) {
        if(listener != null && m_listeners.contains(listener)) {
            m_listeners.remove(listener);
        }
    }

    public void onOpened() {
        for(MenuViewListener listener : m_listeners) {
            listener.onOpened();
        }
    }

    public void onClosed() {
        for(MenuViewListener listener : m_listeners) {
            listener.onClosed();
        }
    }

    public void onOptionExpanded(MenuOption option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onOptionExpanded(option);
        }
    }

    public void onOptionCollapsed(MenuOption option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onOptionCollapsed(option);
        }
    }

    public void onOptionSelected(MenuOption option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onOptionSelected(option);
        }
    }

    public void onChildSelected(MenuChild option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onChildSelected(option);
        }
    }
}
