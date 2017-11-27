// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.searchproviders;

import android.os.AsyncTask;
import android.os.Looper;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.services.poi.OnPoiSearchCompletedListener;
import com.eegeo.mapapi.services.poi.PoiSearch;
import com.eegeo.mapapi.services.poi.PoiSearchResponse;
import com.eegeo.mapapi.services.poi.PoiSearchResult;
import com.eegeo.mapapi.services.poi.PoiService;
import com.eegeo.mapapi.services.poi.TextSearchOptions;
import com.wrld.widgets.searchbox.DefaultSearchResult;
import com.wrld.widgets.searchbox.DefaultSearchResultViewFactory;
import com.wrld.widgets.searchbox.OnResultsReceivedCallback;
import com.wrld.widgets.searchbox.SearchProvider;
import com.wrld.widgets.searchbox.SearchResult;
import com.wrld.widgets.searchbox.SearchResultStringProperty;
import com.wrld.widgets.searchbox.SearchResultViewFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PoiSearchProvider implements SearchProvider, OnPoiSearchCompletedListener {

    private static String m_Title = "Places of Interest";

    private EegeoMap m_map;
    private PoiService m_poiService;

    private PoiSearch m_currentSearch;
    private int m_failedSearches;

    private SearchResultViewFactory m_resultViewFactory;

    private ArrayList<OnResultsReceivedCallback> m_onResultsReceivedCallbacks;

    class RetrieveResultsTask extends AsyncTask<OnPoiSearchCompletedListener, Void, PoiSearchResponse> implements OnPoiSearchCompletedListener {
        private Exception exception;

        PoiSearchResponse m_response;

        private Semaphore m_semaphore;
        private OnPoiSearchCompletedListener m_onCompleteListener;

        @Override
        protected PoiSearchResponse doInBackground(OnPoiSearchCompletedListener... listenerCallback) {
            try {
                m_onCompleteListener = listenerCallback[0];
                m_semaphore = new Semaphore(0);
                m_semaphore.acquire();

                return m_response;
            } catch (Exception e) {
                this.exception = e;
                android.util.Log.e("AsyncTask", exception.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(PoiSearchResponse response) {
            m_onCompleteListener.onPoiSearchCompleted(response);
        }

        @Override
        public void onPoiSearchCompleted(PoiSearchResponse response) {
            m_response = response;
            m_semaphore.release();
        }
    }

    public PoiSearchProvider(PoiService poiApi, EegeoMap map)
    {
        m_poiService = poiApi;
        m_map = map;
        m_onResultsReceivedCallbacks = new ArrayList<OnResultsReceivedCallback>();
    }

    @Override
    public String getTitle() {
        return m_Title;
    }

    @Override
    public void getSearchResults(String query) {
        if(m_currentSearch != null){
            m_currentSearch.cancel();
        }

        RetrieveResultsTask listener = new RetrieveResultsTask();
        listener.execute(this);

        m_currentSearch = m_poiService.searchText(
                new TextSearchOptions(query, m_map.getCameraPosition().target.toLatLng())
                        .radius(1000.0)
                        .number(60)
                        .onPoiSearchCompletedListener(listener));
        //invokeResultsReceived(createDemoModel());
    }

    private ArrayList<SearchResult> createDemoModel(){
        ArrayList<SearchResult> demoResults = new ArrayList<SearchResult>();
        //PoiSearchResultModel demoModel = new PoiSearchResultModel("1", new LatLng(0, 0), "2");
        SearchResult demoModel = new DefaultSearchResult("1", new SearchResultStringProperty("Description", "2"));
        demoResults.add(demoModel);
        return demoResults;
    }

    @Override
    public void onPoiSearchCompleted(PoiSearchResponse response) {
        List<PoiSearchResult> results = response.getResults();

        if (response.succeeded() && results.size() > 0) {
            ArrayList<SearchResult> resultModels = new ArrayList<SearchResult>(results.size());
            for (PoiSearchResult poi : results) {
                resultModels.add(new PoiSearchResultModel(poi.title, poi.latLng, poi.subtitle));
            }
            m_currentSearch = null;
            invokeResultsReceived(resultModels);
        }
        else {
            m_failedSearches += 1;

            if (m_failedSearches >= 3) {
                m_currentSearch = null;
            }
        }
    }

    @Override
    public void addOnResultsRecievedCallback(OnResultsReceivedCallback callback) {
        m_onResultsReceivedCallbacks.add(callback);
    }

    @Override
    public void setResultViewFactory(SearchResultViewFactory factory){
        m_resultViewFactory = factory;
    }

    @Override
    public SearchResultViewFactory getResultViewFactory() {
        return m_resultViewFactory;
    }

    private void invokeResultsReceived(ArrayList<SearchResult> resultList){

        SearchResult[] results = new SearchResult[resultList.size()];
        resultList.toArray(results);

        for(OnResultsReceivedCallback callback : m_onResultsReceivedCallbacks){
            callback.onResultsReceived(results);
        }
    }
}
