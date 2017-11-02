// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wrld.widgets.R;

import java.util.ArrayList;


class DefaultSearchResultSetsContainer {

    private ViewGroup m_resultSetsContainer;
    private LayoutInflater m_inflater;
    private ViewGroup m_setMedium;

    private ArrayList<DefaultSearchResultsContainer> m_searchResultsContainer;

    private SearchResultViewFactory m_defualtViewFactory;

    DefaultSearchResultSetsContainer(ViewGroup resultSetsContainer){
        m_resultSetsContainer = resultSetsContainer;
        m_inflater = LayoutInflater.from(m_resultSetsContainer.getContext());
        m_searchResultsContainer = new ArrayList<DefaultSearchResultsContainer>();
    }

    public void addSearchResultSet(SearchResultSet resultsSet, SearchResultSet suggestionsSet, SearchResultViewFactory factoryResults , SearchResultViewFactory factorySuggestions){



        m_inflater.inflate(R.layout.search_set, m_resultSetsContainer,true);

        ViewGroup setContent = (ViewGroup)m_resultSetsContainer.findViewById(R.id.set_content);
        m_setMedium = (ViewGroup)m_inflater.inflate(R.layout.search_set_medium, setContent,true);

        ListView mediumListView =(ListView)m_setMedium.findViewById(R.id.search_set_content_view);

        LayoutInflater inflator =LayoutInflater.from(mediumListView.getContext());
        final DefaultSearchResultsContainer resultList = new DefaultSearchResultsContainer(inflator,resultsSet , factoryResults, suggestionsSet, factorySuggestions);

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

    }

    public void showSuggestions(boolean flag){
        for(DefaultSearchResultsContainer container: m_searchResultsContainer){
            container.showSuggestions(flag);
        }
    }
}
