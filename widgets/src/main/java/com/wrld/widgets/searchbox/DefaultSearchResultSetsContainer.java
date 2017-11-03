// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public void addSearchResultSet(SearchResultSet resultsSet, SearchResultSet suggestionsSet, SearchResultViewFactory factoryResults , SearchResultViewFactory factorySuggestions){
        
        View setView = m_inflater.inflate(R.layout.search_set, m_resultSetsContainer, false);
        m_resultSetsContainer.addView(setView, m_searchResultsContainer.size() );

        ViewGroup setContent = (ViewGroup)setView.findViewById(R.id.set_content);
        View resultSetContainer = m_inflater.inflate(R.layout.search_set_medium, setContent,true);
        ListView mediumListView = (ListView)resultSetContainer.findViewById(R.id.search_set_content_view);

        final DefaultSearchResultsContainer resultList = new DefaultSearchResultsContainer(
                m_inflater, resultSetContainer,
                resultsSet , factoryResults,
                suggestionsSet, factorySuggestions);

        mediumListView.setAdapter(resultList);

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

        configureButtons(resultSetContainer, resultList);

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
                expandView(resultsSet);
            }
        });
    }

    private void expandView(View resultsSet){
        View expandControls = resultsSet.findViewById(R.id.expand_controls);
        expandControls.setVisibility(View.GONE);
        View paginationControls = resultsSet.findViewById(R.id.pagination_controls);
        paginationControls.setVisibility(View.VISIBLE);
    }

    public void showSuggestions(boolean flag){
        for(DefaultSearchResultsContainer container: m_searchResultsContainer){
            container.showSuggestions(flag);
        }
    }
}
