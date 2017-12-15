package com.wrld.widgets.ui;

import android.view.View;
import android.view.ViewGroup;

public interface AccordionDataProvider {

    interface OnGroupAddedCallback {
        void invoke();
    }

    int getGroupCount();
    int getChildrenCount(int groupPosition);
    AccordionItem getGroup(int groupPosition);
    AccordionItem getChild(int groupPosition, int childPosition);
    View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);
    View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

    void registerGroupAddedCallback(OnGroupAddedCallback groupAddedCallback);
    void deregisterGroupAddedCallback(OnGroupAddedCallback groupAddedCallback);
}
