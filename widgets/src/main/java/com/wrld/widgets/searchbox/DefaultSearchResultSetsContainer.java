// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wrld.widgets.R;


class DefaultSearchResultSetsContainer {

    private ViewGroup m_resultSetsContainer;
    private LayoutInflater m_inflater;
    private ViewGroup m_setMedium;

    private SearchResultViewFactory m_defualtViewFactory;

    DefaultSearchResultSetsContainer(ViewGroup resultSetsContainer){
        m_resultSetsContainer = resultSetsContainer;
        m_inflater = LayoutInflater.from(m_resultSetsContainer.getContext());
        setViewFactory(new DefaultSearchResultViewFactory(R.layout.search_result));
    }

    public void addSearchResultSet(SearchResultSet model, SearchProvider provider, boolean doSuggestion){

        m_inflater.inflate(R.layout.search_set, m_resultSetsContainer,true);

        ViewGroup setContent = (ViewGroup)m_resultSetsContainer.findViewById(R.id.set_content);
        m_setMedium = (ViewGroup)m_inflater.inflate(R.layout.search_set_medium, setContent,true);

        final DefaultSearchResultsContainer resultList = new DefaultSearchResultsContainer((ListView)m_setMedium.findViewById(R.id.search_set_content_view), m_defualtViewFactory);

        resultList.addSearchResultSet(model,  provider.getResultViewFactory());

        model.addOnResultChangedHandler(new SearchResultSet.OnResultChanged() {
            @Override
            public void invoke() {
                resultList.refresh();
            }
        });

    }

    public void setViewFactory(SearchResultViewFactory factory){
        m_defualtViewFactory = factory;
    }
}
