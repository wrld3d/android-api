package com.wrld.widgets.searchbox.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.ISearchResult;

public class DefaultSearchResultViewFactory implements ISearchResultViewFactory {

    private int m_layoutId;

    public DefaultSearchResultViewFactory(int layoutId){
        m_layoutId = layoutId;
    }

    @Override
    public View makeSearchResultView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(m_layoutId, parent, false);
    }

    private class SearchResultViewHolder implements ISearchResultViewHolder {
        private TextView m_title;
        private TextView m_description;

        public SearchResultViewHolder(){}

        public void initialise(View view) {
            m_title = (TextView) view.findViewById(R.id.search_result_title);
            m_description = (TextView) view.findViewById(R.id.search_result_description);
        }

        public void populate(ISearchResult result, String query) {
            m_title.setText(result.getTitle());
            m_description.setText((String)result.getProperty("Description").getValue());
        }
    }

    @Override
    public ISearchResultViewHolder makeSearchResultViewHolder() {
        return new SearchResultViewHolder();
    }
}
