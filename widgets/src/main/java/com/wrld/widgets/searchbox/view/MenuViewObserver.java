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

    public void addMenuListener(MenuViewListener listener) {
        if(listener != null) {
            m_listeners.add(listener);
        }
    }

    public void removeMenuListener(MenuViewListener listener) {
        if(listener != null && m_listeners.contains(listener)) {
            m_listeners.remove(listener);
        }
    }

    public void onMenuOpened() {
        for(MenuViewListener listener : m_listeners) {
            listener.onMenuOpened();
        }
    }

    public void onMenuClosed() {
        for(MenuViewListener listener : m_listeners) {
            listener.onMenuClosed();
        }
    }

    public void onMenuOptionExpanded(MenuOption option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onMenuOptionExpanded(option);
        }
    }

    public void onMenuOptionCollapsed(MenuOption option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onMenuOptionCollapsed(option);
        }
    }

    public void onMenuOptionSelected(MenuOption option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onMenuOptionSelected(option);
        }
    }

    public void onMenuChildSelected(MenuChild option) {
        for(MenuViewListener listener : m_listeners) {
            listener.onMenuChildSelected(option);
        }
    }
}
