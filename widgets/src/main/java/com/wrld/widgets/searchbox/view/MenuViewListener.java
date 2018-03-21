package com.wrld.widgets.searchbox.view;

import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuOption;

public interface MenuViewListener {
    void onOpened();
    void onClosed();
    void onOptionExpanded(MenuOption option);
    void onOptionCollapsed(MenuOption option);
    void onOptionSelected(MenuOption option);
    void onChildSelected(MenuChild option);
}
