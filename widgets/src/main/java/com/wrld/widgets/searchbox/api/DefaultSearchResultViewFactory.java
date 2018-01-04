package com.wrld.widgets.searchbox.api;

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

    private class SearchResultViewHolder implements com.wrld.widgets.searchbox.api.SearchResultViewHolder {
        private TextView m_title;
        private TextView m_description;

        public SearchResultViewHolder(){}

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
    public com.wrld.widgets.searchbox.api.SearchResultViewHolder makeSearchResultViewHolder() {
        return new SearchResultViewHolder();
    }
}
