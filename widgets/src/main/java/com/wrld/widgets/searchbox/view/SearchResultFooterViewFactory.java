package com.wrld.widgets.searchbox.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrld.widgets.R;

public class SearchResultFooterViewFactory {

    private int m_layoutId;

    public SearchResultFooterViewFactory(int layoutId) {
        m_layoutId = layoutId;
    }

    public View makeFooterView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(m_layoutId, parent, false);
    }

    class SearchResultFooterViewHolder {
        private TextView m_title;
        private ImageView m_iconView;

        public SearchResultFooterViewHolder(){}

        public void initialise(View view){
            m_title = (TextView) view.findViewById(R.id.searchbox_set_expand_info);
            m_iconView = (ImageView) view.findViewById(R.id.searchbox_set_expand_button);
        }

        public void showCollapse() {
            String collapsedText = m_iconView.getResources().getString(R.string.search_see_less_results);
            m_title.setText(collapsedText);
            m_iconView.setImageResource(R.drawable.small_back_btn);
        }

        public void showExpand(int hiddenResults, String providerName) {
            String expandedText = m_iconView.getResources().getString(R.string.search_see_more_results, providerName, hiddenResults);
            m_title.setText(expandedText);
            m_iconView.setImageResource(R.drawable.more_results_btn);
        }
    }

    public SearchResultFooterViewHolder makeViewHolder() {
        return new SearchResultFooterViewHolder();
    }
}
