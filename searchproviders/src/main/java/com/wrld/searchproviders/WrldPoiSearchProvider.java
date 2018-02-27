package com.wrld.searchproviders;

import android.content.Context;
import android.net.Uri;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.camera.CameraPosition;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.services.poi.AutocompleteOptions;
import com.eegeo.mapapi.services.poi.OnPoiSearchCompletedListener;
import com.eegeo.mapapi.services.poi.PoiSearch;
import com.eegeo.mapapi.services.poi.PoiSearchResponse;
import com.eegeo.mapapi.services.poi.PoiSearchResult;
import com.eegeo.mapapi.services.poi.PoiService;
import com.eegeo.mapapi.services.poi.TagSearchOptions;
import com.eegeo.mapapi.services.poi.TextSearchOptions;
import com.wrld.widgets.searchbox.model.SearchResult;
import com.wrld.widgets.searchbox.model.SearchResultSelectedListener;
import com.wrld.widgets.searchbox.model.SearchResultProperty;
import com.wrld.widgets.searchbox.view.DefaultSearchResultViewFactory;
import com.wrld.widgets.searchbox.view.DefaultSuggestionViewFactory;
import com.wrld.widgets.searchbox.view.TextHighlighter;

import java.util.List;

public class WrldPoiSearchProvider extends SearchProviderBase {

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
        super(context.getString(R.string.wrld_poi_search_result_title),
              new DefaultSearchResultViewFactory(R.layout.search_result),
              new DefaultSuggestionViewFactory(R.layout.search_suggestion, new TextHighlighter(R.color.black)));
        m_suggestionTitleFormatting = context.getString(R.string.wrld_poi_suggestion_result_title_formatting);
        m_poiService = poiApi;
        m_map = map;
    }

    private class PoiSearchListener implements OnPoiSearchCompletedListener, SearchResultSelectedListener {

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


                    PositionalSearchResult result = new PositionalSearchResult(poi.title,
                            poi.latLng,
                            poi.subtitle,
                            poi.indoorId,
                            poi.floorId,
                            android.R.drawable.ic_dialog_alert);
                    result.setSelectedListener(this);
                    resultsArray[i] = result;

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

        @Override
        public void onSearchResultSelected(SearchResult result) {
            SearchResultProperty<LatLng> position = result.getProperty(SearchPropertyLatLng.Key);
            SearchResultProperty<String> indoorMap = result.getProperty(PositionalSearchResult.IndoorMapKey);
            SearchResultProperty<Integer> indoorFloor = result.getProperty(PositionalSearchResult.IndoorFloorKey);
            int defaultZoomLevel = 18;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position.getValue())
                    .zoom(defaultZoomLevel)
                    .indoor(indoorMap != null ? indoorMap.getValue() : "",
                            indoorFloor != null ? indoorFloor.getValue() : 0)
                    .build();
            m_map.setCameraPosition(cameraPosition);
        }
    }

    @Override
    public void getSearchResults(String searchQuery, Object searchContext) {

        PoiSearchListener listener = new PoiSearchListener(
                new SearchCompleteCallback() {
                    @Override
                    public void invoke(SearchResult[] results) {
                        performSearchCompletedCallbacks(results, true);
                    }
                }
        );

        // TODO: Check searchContext for tag searches
        String tagContext = (String)searchContext;
        if(tagContext != null) {
            m_currentSearch = m_poiService.searchTag(
                    new TagSearchOptions(tagContext, m_map.getCameraPosition().target)
                            .number(20)
                            .onPoiSearchCompletedListener(listener));
        }
        else {
            m_currentSearch = m_poiService.searchText(
                    new TextSearchOptions(Uri.encode(searchQuery), m_map.getCameraPosition().target)
                            .number(20)
                            .onPoiSearchCompletedListener(listener));
        }


    }

    @Override
    public void cancelSearch() {
        m_currentSearch.cancel();
    }

    @Override
    public void getSuggestions(String searchQuery, Object searchContext) {

        if(m_currentSearch != null){
            m_currentSearch.cancel();
        }

        PoiSearchListener listener = new PoiSearchListener(
                new SearchCompleteCallback() {
                    @Override
                    public void invoke(SearchResult[] results) {
                        performSuggestionCompletedCallbacks(results, true);
                    }
                }
        );

        m_currentSearch = m_poiService.searchAutocomplete(
                new AutocompleteOptions(searchQuery, m_map.getCameraPosition().target)
                        .number(5)
                        .onPoiSearchCompletedListener(listener));
    }

    @Override
    public void cancelSuggestions() {
        // TODO: Separate Suggestions and Search providers to be more modular.
        m_currentSearch.cancel();
    }
}
