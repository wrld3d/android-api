// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wrld.widgets.R;
import java.util.ArrayList;

class DefaultSearchResultSetsContainer {

    private ViewGroup m_resultSetsContainer;
    private LayoutInflater m_inflater;

    private ArrayList<DefaultSearchResultsContainer> m_searchResultsContainer;

    private SearchResultViewFactory m_defualtViewFactory;

    DefaultSearchResultSetsContainer(ViewGroup resultSetsContainer){
        m_resultSetsContainer = resultSetsContainer;
        m_inflater = LayoutInflater.from(m_resultSetsContainer.getContext());
        m_searchResultsContainer = new ArrayList<DefaultSearchResultsContainer>();
    }

    public void addSearchResultSet(String titleText, SearchResultSet resultsSet, SearchResultSet suggestionsSet, SearchResultViewFactory factoryResults , SearchResultViewFactory factorySuggestions){
        
        View setView = m_inflater.inflate(R.layout.search_set, m_resultSetsContainer, false);
        m_resultSetsContainer.addView(setView, m_searchResultsContainer.size() );

        View setContent = setView.findViewById(R.id.set_content);
        ListView listView = (ListView) setContent.findViewById(R.id.search_result_list);

        final DefaultSearchResultsContainer resultList = new DefaultSearchResultsContainer(
                m_inflater, setView,
                resultsSet , factoryResults,
                suggestionsSet, factorySuggestions);

        listView.setAdapter(resultList);

        resultsSet.addOnResultChangedHandler(new SearchResultSet.OnResultChanged() {
            @Override
            public void invoke() {
                resultList.refresh();
            }
        });

        suggestionsSet.addOnResultChangedHandler(new SearchResultSet.OnResultChanged() {
            @Override
            public void invoke() {
                resultList.refresh();
            }
        });

        m_searchResultsContainer.add(resultList);

        configureButtons(setView, resultList);

        TextView title = (TextView)setView.findViewById(R.id.serach_set_title);
        title.setText(titleText);
    }

    private void configureButtons(final View resultsSet, final DefaultSearchResultsContainer resultsAdapter) {
        Button next = (Button) resultsSet.findViewById(R.id.search_pagination_prev);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resultsAdapter.previousPage();
            }
        });
        Button prev = (Button) resultsSet.findViewById(R.id.search_pagination_next);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resultsAdapter.nextPage();
            }
        });
        Button expand = (Button) resultsSet.findViewById(R.id.search_expand_button);
        expand.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                expandView(resultsAdapter);
            }
        });
    }

    private void expandView(DefaultSearchResultsContainer resultsSet){
        for(DefaultSearchResultsContainer container: m_searchResultsContainer){
            container.setState(resultsSet == container ? ResultSetState.Expanded : ResultSetState.Collapsed);
        }
    }

    public void showSuggestions(boolean flag){
        for(DefaultSearchResultsContainer container: m_searchResultsContainer){
            container.setState(ResultSetState.Shared);
            container.showSuggestions(flag);
        }
    }
}
