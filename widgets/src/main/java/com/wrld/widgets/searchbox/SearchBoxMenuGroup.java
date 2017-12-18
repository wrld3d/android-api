package com.wrld.widgets.searchbox;

import java.util.ArrayList;

public final class SearchBoxMenuGroup extends SearchBoxMenuItemBase {

    private ArrayList<SearchBoxMenuChild> m_children;

    public int size() { return m_children.size(); }

    public SearchBoxMenuGroup(String text){
        super(text);
        m_children = new ArrayList<SearchBoxMenuChild>();
    }

    public void add(SearchBoxMenuChild child){
        m_children.add(child);
    }

    public void add(SearchBoxMenuChild[] children){
        for(SearchBoxMenuChild child : children){
            m_children.add(child);
        }
    }

    public SearchBoxMenuChild get(int index){
        return m_children.get(index);
    }

    public void addOnClickListenerToAllChildren(OnClickListener onClickListener){
        for(SearchBoxMenuChild child : m_children){
            child.addOnClickListener(onClickListener);
        }
    }

    public void removeOnClickListenerFromAllChildren(OnClickListener onClickListener){
        for(SearchBoxMenuChild child : m_children){
            child.removeOnClickListener(onClickListener);
        }
    }
}
