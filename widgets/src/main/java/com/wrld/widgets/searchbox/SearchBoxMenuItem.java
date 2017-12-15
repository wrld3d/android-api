package com.wrld.widgets.searchbox;

import com.wrld.widgets.ui.AccordionItem;

public interface SearchBoxMenuItem extends AccordionItem {
    interface OnClickListener{
        void onClick(SearchBoxMenuItem clickedItem);
    }

    String getTitle();
    void addOnClickListener(OnClickListener onClickListener);
    void removeOnClickListener(OnClickListener onClickListener);

    void clicked();
}