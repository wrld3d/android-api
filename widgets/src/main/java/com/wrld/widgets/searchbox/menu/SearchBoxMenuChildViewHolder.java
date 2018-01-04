package com.wrld.widgets.searchbox.menu;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrld.widgets.R;

public class SearchBoxMenuChildViewHolder {
    private TextView m_title;
    private ImageView m_icon;

    public SearchBoxMenuChildViewHolder(View root){
        m_title = (TextView) root.findViewById(R.id.searchbox_menu_item_text);
        m_icon = (ImageView) root.findViewById(R.id.searchbox_menu_item_icon);
    }

    public void populate(String text){
        m_title.setText(text);
    }
}
