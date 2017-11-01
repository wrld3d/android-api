// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wrld.widgets.R;


class DefaultSearchResultSetsContainer {

    private ViewGroup m_resultSetsContainer;
    private LayoutInflater m_inflater;
    DefaultSearchResultSetsContainer(ViewGroup resultSetsContainer){
        m_resultSetsContainer = resultSetsContainer;
        m_inflater = LayoutInflater.from(m_resultSetsContainer.getContext());
    }

    public void addSearchResultSet(SearchResultSet model, SearchResultViewFactory factory){

        m_inflater.inflate(R.layout.search_set,m_resultSetsContainer,true);

        model.addOnResultChangedHandler(new SearchResultSet.OnResultChanged() {
            @Override
            public void invoke() {
                UpdateView();
            }
        });

    }

    private void UpdateView() {
        //TODO: Update View
        android.util.Log.v("SA ","View Updated ");
    }
}
