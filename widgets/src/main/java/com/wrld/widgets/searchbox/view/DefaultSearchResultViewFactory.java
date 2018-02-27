package com.wrld.widgets.searchbox.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.SearchResult;

public class DefaultSearchResultViewFactory implements ISearchResultViewFactory {

    public static String DescriptionKey = "Description";
    public static String IconResourceKey = "IconResource";

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
        private ImageView m_icon;

        private View m_divider;
        private View m_separator;
        private View m_shadow;

        public SearchResultViewHolder() {
        }

        public void initialise(View view) {
            m_title = (TextView) view.findViewById(R.id.search_result_title);
            m_description = (TextView) view.findViewById(R.id.search_result_description);
            m_icon = (ImageView) view.findViewById(R.id.search_result_icon);

            m_divider = view.findViewById(R.id.search_result_divider);
            m_separator = view.findViewById(R.id.search_result_top_seperator);
            m_shadow = view.findViewById(R.id.search_result_shadow);
        }

        public void populate(SearchResult result,
                             String query,
                             boolean firstResultInSet,
                             boolean lastResultInSet) {
            m_title.setText(result.getTitle());
            if(result.hasProperty(DescriptionKey)) {
                m_description.setText((String)result.getProperty(DescriptionKey).getValue());
                m_description.setVisibility(View.VISIBLE);
            }
            else {
                m_description.setVisibility(View.GONE);
            }

            if(result.hasProperty(IconResourceKey)) {
                int resourceId = (int)result.getProperty(IconResourceKey).getValue();
                m_icon.setImageResource(resourceId);
            }
            else {
                m_icon.setImageResource(android.R.drawable.ic_menu_search);
            }

            m_divider.setVisibility(lastResultInSet ? View.GONE : View.VISIBLE);
            m_separator.setVisibility(firstResultInSet ? View.VISIBLE : View.GONE);
            m_shadow.setVisibility(firstResultInSet ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public ISearchResultViewHolder makeSearchResultViewHolder() {
        return new SearchResultViewHolder();
    }
}
