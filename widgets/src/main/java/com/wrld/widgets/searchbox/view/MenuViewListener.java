package com.wrld.widgets.searchbox.view;

import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuOption;

public interface MenuViewListener {
    void onMenuOpened();
    void onMenuClosed();
    void onMenuOptionExpanded(MenuOption option);
    void onMenuOptionCollapsed(MenuOption option);
    void onMenuOptionSelected(MenuOption option);
    void onMenuChildSelected(MenuChild option);
}
