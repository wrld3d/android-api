package com.wrld.widgets.searchbox.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.ISearchResult;

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
            m_title.setText("Back");
            m_iconView.setImageResource(R.drawable.back_btn);
        }

        public void showExpand(int hiddenResults, String providerName) {
            m_title.setText("See more " + providerName + " (" + hiddenResults + ") results");
            m_iconView.setImageResource(R.drawable.moreresults_butn);
        }
    }

    public SearchResultFooterViewHolder makeViewHolder() {
        return new SearchResultFooterViewHolder();
    }
}
