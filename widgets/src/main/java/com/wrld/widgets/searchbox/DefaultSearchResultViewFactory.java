// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wrld.widgets.R;

public class DefaultSearchResultViewFactory implements SearchResultViewFactory {

    private int m_layoutId;

    public DefaultSearchResultViewFactory(int layoutId){
        m_layoutId = layoutId;
    }

    @Override
    public View makeSearchResultView(LayoutInflater inflater, SearchResult searchResult, ViewGroup parent) {
        return inflater.inflate(m_layoutId, parent, false);
    }

    private class SearchResultViewHolderImpl implements SearchResultViewHolder {
        private TextView m_title;
        private TextView m_description;

        public SearchResultViewHolderImpl(){}

        public void initialise(View view) {
            m_title = (TextView) view.findViewById(R.id.search_result_title);
            m_description = (TextView) view.findViewById(R.id.search_result_description);
        }

        public void populate(SearchResult result) {
            m_title.setText(result.getTitle());
            m_description.setText((String)result.getProperty("Description").getValue());
        }
    }

    @Override
    public SearchResultViewHolder makeSearchResultViewHolder() {
        return new SearchResultViewHolderImpl();
    }
}
