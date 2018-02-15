package com.wrld.widgets.searchbox.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.SearchResult;

public class DefaultSuggestionViewFactory implements ISearchResultViewFactory {

    private int m_layoutId;
    private TextHighlighter m_highlighter;

    public DefaultSuggestionViewFactory(int layoutId) {
        m_layoutId = layoutId;
    }

    public DefaultSuggestionViewFactory(int layoutId, TextHighlighter highlighter){
        m_layoutId = layoutId;
        m_highlighter = highlighter;
    }

    @Override
    public View makeSearchResultView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(m_layoutId, parent, false);
    }

    private class SearchResultViewHolder implements ISearchResultViewHolder {
        private TextView m_title;
        private TextHighlighter m_highlighter;

        public SearchResultViewHolder(TextHighlighter highlighter){
            m_highlighter = highlighter;
        }

        public void initialise(View view){
            m_title = (TextView) view.findViewById(R.id.search_result_title);
        }

        public void populate(SearchResult result,
                             String searchTerm,
                             boolean firstResultInSet,
                             boolean lastResultInSet) {
            if(m_highlighter != null) {
                m_highlighter.format(m_title, result.getTitle(), searchTerm);
            }
            else {
                m_title.setText(result.getTitle());
            }
        }
    }

    @Override
    public ISearchResultViewHolder makeSearchResultViewHolder() {
        return new SearchResultViewHolder(m_highlighter);
    }

}