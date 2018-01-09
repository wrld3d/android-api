package com.wrld.widgets.searchbox.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wrld.widgets.R;
import com.wrld.widgets.ui.TextHighlighter;

public class DefaultSuggestionViewFactory implements SearchResultViewFactory {

    private int m_layoutId;
    private SearchQueryHandler m_queryHandler;

    private TextHighlighter m_highlighter;

    public DefaultSuggestionViewFactory(int layoutId) {
        m_layoutId = layoutId;
    }

    public DefaultSuggestionViewFactory(int layoutId, SearchQueryHandler queryHandler, TextHighlighter highlighter){
        m_layoutId = layoutId;
        m_queryHandler = queryHandler;
        m_highlighter = highlighter;
    }

    @Override
    public View makeSearchResultView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(m_layoutId, parent, false);
    }

    private class SearchResultViewHolder implements com.wrld.widgets.searchbox.api.SearchResultViewHolder {
        private TextView m_title;

        public SearchResultViewHolder(){}

        public void initialise(View view){
            m_title = (TextView) view.findViewById(R.id.search_result_title);
        }

        public void populate(SearchResult result){
            if(m_queryHandler != null){
                highlightMatchingQueryComponent(result.getTitle());
            }
            else{
                m_title.setText(result.getTitle());
            }
        }

        private void highlightMatchingQueryComponent(String content){
            String query = m_queryHandler.getCurrentQuery().toString();
            m_highlighter.format(m_title, content, query);
        }
    }

    @Override
    public com.wrld.widgets.searchbox.api.SearchResultViewHolder makeSearchResultViewHolder() {
        return new SearchResultViewHolder();
    }

}
