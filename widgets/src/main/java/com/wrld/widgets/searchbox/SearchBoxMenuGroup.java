package com.wrld.widgets.searchbox;

import java.util.ArrayList;

public final class SearchBoxMenuGroup extends SearchBoxMenuItemBase {

    private ArrayList<SearchBoxMenuChild> m_children;

    public int size() { return m_children.size(); }

    public SearchBoxMenuGroup(String text){
        super(text);
        m_children = new ArrayList<SearchBoxMenuChild>();
    }

    public SearchBoxMenuChild add(String childText){
        SearchBoxMenuChild child = new SearchBoxMenuChild(childText);
        m_children.add(child);
        return child;
    }

    public SearchBoxMenuItem[] add(String[] childTexts){
        ArrayList<SearchBoxMenuChild> newChildren = new ArrayList<SearchBoxMenuChild>();
        for(String childText : childTexts){
            newChildren.add(add(childText));
        }
        SearchBoxMenuChild[] newChildrenArray = new SearchBoxMenuChild[newChildren.size()];
        return newChildren.toArray(newChildrenArray);
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
