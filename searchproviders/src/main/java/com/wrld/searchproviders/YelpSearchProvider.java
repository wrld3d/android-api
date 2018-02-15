package com.wrld.searchproviders;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.camera.CameraPosition;
import com.eegeo.mapapi.geometry.LatLng;
import com.wrld.widgets.searchbox.model.DefaultSearchResult;
import com.wrld.widgets.searchbox.model.SearchResult;
import com.wrld.widgets.searchbox.model.SearchResultSelectedListener;
import com.wrld.widgets.searchbox.model.SearchResultProperty;
import com.wrld.widgets.searchbox.view.DefaultSuggestionViewFactory;
import com.wrld.widgets.searchbox.view.TextHighlighter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class YelpSearchProvider extends SearchProviderBase implements SearchResultSelectedListener {

    private String m_suggestionsTitleFormatting;


    @Override
    public String getSuggestionTitleFormatting() {
        return m_suggestionsTitleFormatting;
    }

    private static final String m_authUrl = "https://api.yelp.com/oauth2/token";
    private static final String m_searchUrl = "https://api.yelp.com/v3/businesses/search";
    private static final String m_autocompleteUrl = "https://api.yelp.com/v3/autocomplete";

    private static final String m_authTokenKey = "access_token";
    private static final String m_resultsArrayKey = "businesses";

    private static final int m_searchRadiusInMeters = 500;
    private static final int m_initialResultCount = 40;

    private static final int m_timeoutMs = 10000;
    private static final int m_maxRetries = 3;

    private EegeoMap m_map;

    private String m_yelpApiKey;

    private Map<String, String> m_authHeaders;
    private boolean m_hasRaisedAuthError = false;

    private RequestQueue m_requestQueue;

    private ErrorHandler m_errorHandler;

    private Response.Listener<String> m_searchResponseListener;
    private Response.Listener<String> m_autocompleteResponseListener;
    private Response.ErrorListener m_errorListener;

    private boolean m_assignedApiKey;

    private Request m_currentRequest;

    public YelpSearchProvider(Context context, RequestQueue requestQueue, EegeoMap map, ErrorHandler errorHandler){
        super(context.getString(R.string.yelp_search_result_title),
                new YelpSearchResultViewFactory(context),
                new DefaultSuggestionViewFactory(R.layout.search_suggestion, new TextHighlighter(R.color.black)));
        m_suggestionsTitleFormatting = context.getString(R.string.yelp_suggestion_formatting);

        m_requestQueue = requestQueue;
        m_map = map;
        m_errorHandler = errorHandler;
        m_hasRaisedAuthError = false;

        m_assignedApiKey = false;
        createCallbacks();
    }

    public void authenticate(String yelpApiKey) {
        m_yelpApiKey = yelpApiKey;
        m_assignedApiKey = true;
        m_authHeaders = new HashMap<String, String>();
        m_authHeaders.put("Authorization", "Bearer " + m_yelpApiKey);
    }

    @Override
    public void getSearchResults(String queryString, Object queryContext) {
        if(m_assignedApiKey) {

            LatLng cameraPosition = m_map.getCameraPosition().target;

            String queryText = queryString;
            try {
                queryText = URLEncoder.encode(queryText, "utf-8");
            }
            catch (UnsupportedEncodingException ex){
                m_errorHandler.handleError(R.string.yelp_search_error_title, R.string.yelp_error_badly_formatted_query);
            }
            String queryUrl = m_searchUrl + queryParams(queryText, cameraPosition, m_searchRadiusInMeters);

            m_currentRequest = new VolleyStringRequestBuilder(
                    Request.Method.GET, queryUrl, m_searchResponseListener)
                    .setHeaders(m_authHeaders)
                    .setRetryPolicy(m_timeoutMs, m_maxRetries)
                    .setErrorListener(m_errorListener)
                    .build();

            m_requestQueue.add(m_currentRequest);
        }
        else {
            handleUnauthorisedRequest();
            performSuggestionCompletedCallbacks(new SearchResult[0], false);
        }
    }

    @Override
    public void cancelSearch() {
        m_currentRequest.cancel();
    }

    @Override
    public void getSuggestions(String queryString, Object queryContext) {

        if(m_assignedApiKey) {

            if(m_currentRequest != null && !m_currentRequest.hasHadResponseDelivered()){
                m_currentRequest.cancel();
            }

            LatLng cameraPosition = m_map.getCameraPosition().target;
            String queryText = queryString;
            try {
                queryText = URLEncoder.encode(queryText, "utf-8");
            }
            catch (UnsupportedEncodingException ex){
                return;
            }

            String queryUrl = m_autocompleteUrl + queryParams(queryText, cameraPosition);

            m_currentRequest = new VolleyStringRequestBuilder(
                    Request.Method.GET, queryUrl, m_autocompleteResponseListener)
                    .setRetryPolicy(m_timeoutMs, m_maxRetries)
                    .setHeaders(m_authHeaders)
                    .setErrorListener(m_errorListener).build();

            m_requestQueue.add(m_currentRequest);
        }
        else {
            handleUnauthorisedRequest();
        }
    }

    @Override
    public void cancelSuggestions() {
        m_currentRequest.cancel();
    }

    private String queryParams(String text, LatLng location){
        return "?text=" + text +
                "&latitude=" + location.latitude +
                "&longitude=" + location.longitude;
    }

    private String queryParams(String text, LatLng location, int radiusMeters){
        return "?term=" + text +
                "&latitude=" + location.latitude +
                "&longitude=" + location.longitude +
                "&radius=" + radiusMeters +
                "&limit=" + m_initialResultCount;
    }

    private void suggestionResponseHandler(JSONObject response){
        JSONArray businesses = response.optJSONArray(m_resultsArrayKey);
        SearchResult[] results = new SearchResult[businesses.length()];
        for(int i = 0; i < businesses.length(); ++i){
            JSONObject businessJson = businesses.optJSONObject(i);
            results[i] = new DefaultSearchResult(businessJson.optString("name"));
        }

        performSuggestionCompletedCallbacks(results, true);
    }

    private void searchResponseHandler(JSONObject response){
        JSONArray businesses = response.optJSONArray(m_resultsArrayKey);
        SearchResult[] results = new SearchResult[businesses.length()];
        for(int i = 0; i < businesses.length(); ++i){
            JSONObject businessJson = businesses.optJSONObject(i);
            YelpSearchResult result = new YelpSearchResult(businessJson);
            result.setSelectedListener(this);
            results[i] = result;
        }

        performSearchCompletedCallbacks(results, true);
    }

    private void createCallbacks(){

        m_searchResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    searchResponseHandler(new JSONObject(response));
                } catch (JSONException e) {
                    android.util.Log.w("Yelp Search", "Search Response Error: " +  response);
                    performSearchCompletedCallbacks(new SearchResult[0], false);
                }
            }
        };

        m_errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                performSearchCompletedCallbacks(new SearchResult[0], false);
            }
        };

        m_autocompleteResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    suggestionResponseHandler(new JSONObject(response));
                } catch (JSONException e) {
                    android.util.Log.w("Yelp Search", "Suggestion Response Error: " +  response);
                    performSuggestionCompletedCallbacks(new SearchResult[0], false);
                }
            }
        };
    }

    private void handleUnauthorisedRequest(){
        if(!m_hasRaisedAuthError) {
            m_errorHandler.handleError(R.string.yelp_search_error_title, R.string.yelp_error_unauthorised_search);
            m_hasRaisedAuthError = true;
        }
    }

    @Override
    public void onSearchResultSelected(SearchResult result) {
        SearchResultProperty<LatLng> position = result.getProperty(SearchPropertyLatLng.Key);
        int defaultZoomLevel = 18;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position.getValue())
                .zoom(defaultZoomLevel)
                .build();
        m_map.setCameraPosition(cameraPosition);
    }
}
