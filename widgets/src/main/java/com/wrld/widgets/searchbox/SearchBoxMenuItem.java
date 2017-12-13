package com.wrld.widgets.searchbox;

public interface SearchBoxMenuItem {
    interface OnClickListener{
        void onClick(SearchBoxMenuItem clicked);
    }

    String getTitle();
    void addOnClickListener(OnClickListener onClickListener);
    void removeOnClickListener(OnClickListener onClickListener);

    void clicked();
}