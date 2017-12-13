package com.wrld.widgets.searchbox;

import android.view.View;

import java.util.ArrayList;

abstract class SearchBoxMenuItemBase implements SearchBoxMenuItem {
    private String m_text;

    private ArrayList<OnClickListener> m_onClickListeners;

    public String getTitle() { return m_text; }

    public SearchBoxMenuItemBase(String text){
        m_text = text;
        m_onClickListeners = new ArrayList<OnClickListener>();
    }

    public void addOnClickListener(OnClickListener onClickListener){
        m_onClickListeners.add(onClickListener);
    }

    public void removeOnClickListener(OnClickListener onClickListener){
        m_onClickListeners.remove(onClickListener);
    }

    public void clicked(){
        for(OnClickListener clickListener : m_onClickListeners){
            clickListener.onClick(this);
        }
    }
}