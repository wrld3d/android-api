package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.SearchResult;

import java.util.ArrayList;

class SetCollection {

    private class SearchResultIndex{
        private int m_setIndex;
        private int m_positionWithinSet;
        public int getSet() {return m_setIndex; }
        public int getPosition() { return m_positionWithinSet; }
        public SearchResultIndex(int setIndex, int position){
            m_setIndex = setIndex;
            m_positionWithinSet = position;
        }
    }

    ArrayList<SearchResultSet> m_sets;

    public SetCollection() {
        m_sets= new ArrayList<SearchResultSet>();
    }

    public void setSets(ArrayList<SearchResultSet> sets){
        m_sets.clear();
        m_sets.addAll(sets);
    }

    private SearchResultIndex getSearchResultIndex(int position){
        int count = 0;

        int setIndex = 0;
        for(SearchResultSet set : m_sets){
            int actualPositionInSet = position - count;
            int setSize = set.getResultCount();
            if(actualPositionInSet >= setSize){
                count += setSize;
                ++setIndex;
            }
            else {
                return new SearchResultIndex(setIndex, actualPositionInSet);
            }
        }

        return null;
    }

    public int getSetAtIndex(int position){
        SearchResultIndex index = getSearchResultIndex(position);
        return index.getSet();
    }

    public SearchResult getResultAtIndex(int position){
        SearchResultIndex index = getSearchResultIndex(position);
        return m_sets.get(index.getSet()).getResult(index.getPosition());
    }

    public int getCount() {
        int totalResults = 0;
        for(SearchResultSet set : m_sets){
            totalResults += set.getResultCount();
        }
        return totalResults;
    }
}
