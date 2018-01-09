package com.wrld.widgets.searchbox;

import com.wrld.widgets.searchbox.api.SearchResult;

import java.util.ArrayList;

class SetCollection {

    public interface OnResultChanged{
        void invoke(int setChangedIndex);
    }

    private ArrayList<OnResultChanged> m_onResultChangedCallbacks;

    private boolean m_hasExpandedSet = false;

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
        m_onResultChangedCallbacks = new ArrayList<OnResultChanged>();
    }

    public void setSets(ArrayList<SearchResultSet> sets){
        m_sets.clear();
        m_sets.addAll(sets);

        for(int i = 0; i < sets.size(); ++i){
            SearchResultSet set = sets.get(i);
            final int setId = i;
            set.addOnResultChangedHandler(new SearchResultSet.OnResultChanged(){
                @Override
                public void invoke() {
                    for(OnResultChanged callback : m_onResultChangedCallbacks){
                        callback.invoke(setId);
                    }
                }
            });
        }
    }

    private SearchResultIndex getSearchResultIndex(int position){
        int count = 0;

        int setIndex = 0;
        for(SearchResultSet set : m_sets){
            int actualPositionInSet = position - count;
            int setSize = set.getVisibleResultCount();
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

    public int getSetForAbsolutePosition(int position){
        SearchResultIndex index = getSearchResultIndex(position);
        return index.getSet();
    }

    public SearchResultSet getSet(int index){
        return m_sets.get(index);
    }

    public SearchResult getResultAtIndex(int position){
        SearchResultIndex index = getSearchResultIndex(position);
        return m_sets.get(index.getSet()).getResult(index.getPosition());
    }

    public SearchResult getResultAtIndex(int groupIndex, int childIndex){
        return m_sets.get(groupIndex).getResult(childIndex);
    }

    public int getSetCount(){
        return m_sets.size();
    }

    public int getChildrenInSetCount(int set){
        return m_sets.get(set).getVisibleResultCount();
    }

    public int getAllResultsCount() {
        int totalResults = 0;
        for(SearchResultSet set : m_sets){
            totalResults += set.getVisibleResultCount();
        }
        return totalResults;
    }

    public void addOnResultChangedHandler(OnResultChanged callback) {
        m_onResultChangedCallbacks.add(callback);
    }

    public void expandResultSet(int groupPosition) {
        m_hasExpandedSet = true;
        for(int i = 0; i < m_sets.size(); ++i){
            m_sets.get(i).setExpansionState(i == groupPosition ? SearchResultSet.ExpansionState.EXPANDED : SearchResultSet.ExpansionState.HIDDEN);
        }
    }

    public boolean hasExpandedSet(){
        return m_hasExpandedSet;
    }

    public void collapseResultSets(){
        m_hasExpandedSet = false;
        for(int i = 0; i < m_sets.size(); ++i){
            m_sets.get(i).setExpansionState(SearchResultSet.ExpansionState.COLLAPSED);
        }
    }
}
