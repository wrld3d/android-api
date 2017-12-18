package com.wrld.widgets.searchbox;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrld.widgets.R;

public class SearchBoxMenuGroupViewHolder {
    private TextView m_title;
    private ImageView m_icon;
    private ImageView m_shadow;

    public SearchBoxMenuGroupViewHolder(View root){
        m_title = (TextView) root.findViewById(R.id.searchbox_menu_item_text);
        m_icon = (ImageView) root.findViewById(R.id.searchbox_menu_group_icon);
        m_shadow = (ImageView) root.findViewById(R.id.searchbox_menu_contents_shadow);
    }

    public void populate(String text, int iconImageResource, float rotation, boolean hideShadow){
        m_title.setText(text);
        m_icon.setImageResource(iconImageResource);
        m_icon.setRotation(rotation);
        m_shadow.setVisibility(hideShadow ? View.GONE : View.VISIBLE);
    }
}
