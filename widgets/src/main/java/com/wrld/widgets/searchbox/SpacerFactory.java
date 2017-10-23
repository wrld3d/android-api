// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class SpacerFactory implements SearchResultViewFactory {

    private int m_layoutId;

    public SpacerFactory (int layoutId){
        m_layoutId = layoutId;
    }

    public SpacerFactory (int layoutId, int spacerSize){
        m_layoutId = layoutId;
    }

    @Override
    public View makeSearchResultView(LayoutInflater inflater, SearchResult searchResult, ViewGroup parent) {
        View spacer = inflater.inflate(m_layoutId, parent, false);
        return spacer;
    }

    private class SearchResultViewHolderImpl implements SearchResultViewHolder {

        public SearchResultViewHolderImpl(){}

        public void initialise(View view){
        }

        public void populate(SearchResult result){
        }
    }

    @Override
    public SearchResultViewHolder makeSearchResultViewHolder() {
        return new SearchResultViewHolderImpl();
    }
}
