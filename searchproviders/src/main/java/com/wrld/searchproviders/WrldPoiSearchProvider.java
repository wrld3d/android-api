package com.wrld.searchproviders;

import android.content.Context;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.services.poi.AutocompleteOptions;
import com.eegeo.mapapi.services.poi.OnPoiSearchCompletedListener;
import com.eegeo.mapapi.services.poi.PoiSearch;
import com.eegeo.mapapi.services.poi.PoiSearchResponse;
import com.eegeo.mapapi.services.poi.PoiSearchResult;
import com.eegeo.mapapi.services.poi.PoiService;
import com.eegeo.mapapi.services.poi.TextSearchOptions;
import com.wrld.widgets.searchbox.SearchResult;
import com.wrld.widgets.searchbox.SuggestionProviderBase;

import java.util.List;

public class WrldPoiSearchProvider extends SuggestionProviderBase {

    private EegeoMap m_map;
    private PoiService m_poiService;

    private PoiSearch m_currentSearch;
    private int m_failedSearches;

    private String m_suggestionTitleFormatting;

    private interface SearchCompleteCallback {
        void invoke(SearchResult[] results);
    }

    @Override
    public String getSuggestionTitleFormatting() {
        return m_suggestionTitleFormatting;
    }

    public WrldPoiSearchProvider(Context context, PoiService poiApi, EegeoMap map)
    {
        super(context.getString(R.string.wrld_poi_search_result_title));
        m_suggestionTitleFormatting = context.getString(R.string.wrld_poi_suggestion_result_title_formatting);
        m_poiService = poiApi;
        m_map = map;
    }

    private class PoiSearchListener implements OnPoiSearchCompletedListener {

        private SearchCompleteCallback m_searchCompleteCallback;

        public PoiSearchListener(SearchCompleteCallback searchCompleteCallback){
            m_searchCompleteCallback = searchCompleteCallback;
        }

        @Override
        public void onPoiSearchCompleted(PoiSearchResponse response) {
            List<PoiSearchResult> results = response.getResults();

            if (response.succeeded()) {
                SearchResult[] resultsArray = new SearchResult[results.size()];
                for (int i = 0; i < results.size(); ++i) {
                    PoiSearchResult poi = results.get(i);
                    resultsArray[i] = new PositionalSearchResult(poi.title, poi.latLng, poi.subtitle);
                }
                m_currentSearch = null;
                m_searchCompleteCallback.invoke(resultsArray);
            }
            else {
                m_failedSearches += 1;

                if (m_failedSearches >= 1) {
                    m_currentSearch = null;
                    m_searchCompleteCallback.invoke(new SearchResult[0]);
                }
            }
        }
    }

    @Override
    public void getSearchResults(String query) {

        PoiSearchListener listener = new PoiSearchListener(
                new SearchCompleteCallback() {
                    @Override
                    public void invoke(SearchResult[] results) {
                        performSearchCompletedCallbacks(results);
                    }
                }
        );

        m_currentSearch = m_poiService.searchText(
                new TextSearchOptions(query, m_map.getCameraPosition().target.toLatLng())
                        .radius(1000.0)
                        .number(60)
                        .onPoiSearchCompletedListener(listener));
    }

    @Override
    public boolean hasActiveRequest() {
        return m_currentSearch != null;
    }

    @Override
    public void cancelActiveRequest() {
        if(m_currentSearch != null){
            m_currentSearch.cancel();
        }
    }

    @Override
    public void getSuggestions(String query) {

        //TODO test poi search invokes callbacks
        //TODO test poi search response without network

        if(m_currentSearch != null){
            m_currentSearch.cancel();
        }

        PoiSearchListener listener = new PoiSearchListener(
                new SearchCompleteCallback() {
                    @Override
                    public void invoke(SearchResult[] results) {
                        performSuggestionCompletedCallbacks(results);
                    }
                }
        );

        m_currentSearch = m_poiService.searchAutocomplete(
                new AutocompleteOptions(query, m_map.getCameraPosition().target.toLatLng())
                        .number(5)
                        .onPoiSearchCompletedListener(listener));
    }
}
