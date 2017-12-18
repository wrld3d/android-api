package com.wrld.searchproviders;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.geometry.LatLngAlt;
import com.wrld.widgets.searchbox.DefaultSearchResult;
import com.wrld.widgets.searchbox.SearchResult;
import com.wrld.widgets.searchbox.SuggestionProviderBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class YelpSearchProvider extends SuggestionProviderBase {

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

    private String m_yelpApiClientKey;
    private String m_yelpApiClientSecret;

    private enum AuthStatus {UNAUTHENTICATED, AUTHENTICATING, AUTHENTICATED, TIMED_OUT, REJECTED };
    private AuthStatus m_authStatus;
    public boolean isAuthenticated () { return m_authStatus == AuthStatus.AUTHENTICATED; }
    private Map<String, String> m_authHeaders;
    private boolean m_hasRaisedAuthError = false;

    private enum SearchType {SEARCH, AUTOCOMPLETE };
    private SearchType m_deferredSearchType;
    private String m_deferredQuery;

    private RequestQueue m_requestQueue;

    private ErrorHandler m_errorHandler;

    private Response.Listener<String> m_authResponseListener;
    private Response.Listener<String> m_searchResponseListener;
    private Response.Listener<String> m_autocompleteResponseListener;
    private Response.ErrorListener m_authErrorListener;

    private Request m_currentRequest;

    public YelpSearchProvider(Context context, RequestQueue requestQueue, EegeoMap map, ErrorHandler errorHandler){
        super(context.getString(R.string.yelp_search_result_title));
        m_suggestionsTitleFormatting = context.getString(R.string.yelp_suggestion_formatting);
        m_authStatus = AuthStatus.UNAUTHENTICATED;

        m_requestQueue = requestQueue;
        m_map = map;
        m_errorHandler = errorHandler;
        m_hasRaisedAuthError = false;

        createCallbacks();
    }

    public void authenticate(String yelpApiClientKey, String yelpApiClientSecret) {

        m_yelpApiClientKey = yelpApiClientKey;
        m_yelpApiClientSecret = yelpApiClientSecret;

        if(m_authStatus == AuthStatus.AUTHENTICATING){
            m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_auth_retry_while_in_progress);
            return;
        }

        if(m_authStatus == AuthStatus.AUTHENTICATED){
            m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_auth_retry_while_authenticated);
            return;
        }

        HashMap<String, String> postArgs = new HashMap<String, String>();

        postArgs.put("grant_type", "client_credentials");
        postArgs.put("client_id", yelpApiClientKey);
        postArgs.put("client_secret", yelpApiClientSecret);

        StringRequest request =  new VolleyStringRequestBuilder(
                Request.Method.POST,
                m_authUrl,
                m_authResponseListener)
                .setParams(postArgs)
                .setErrorListener(m_authErrorListener)
                .setRetryPolicy(m_timeoutMs, m_maxRetries)
                .build();

        m_authStatus = AuthStatus.AUTHENTICATING;
        m_requestQueue.add(request);
    }

    private void authenticationResponseHandler(JSONObject response) {
        if(response.has(m_authTokenKey)) {
            String accessToken = response.optString(m_authTokenKey);
            if(TextUtils.isEmpty(accessToken)){
                m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_empty_auth_token);
                m_authStatus = AuthStatus.REJECTED;
                return;
            }
            m_authStatus = AuthStatus.AUTHENTICATED;
            m_authHeaders = new HashMap<String, String>();
            m_authHeaders.put("Authorization", "Bearer " + accessToken);

            if(!TextUtils.isEmpty(m_deferredQuery)){
                sendDeferredQuery();
            }
        }
        else{
            m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_no_auth_token);
            m_authStatus = AuthStatus.REJECTED;
        }
    }

    @Override
    public void getSearchResults(String query) {

        if(isAuthenticated()) {

            if(m_currentRequest != null && !m_currentRequest.hasHadResponseDelivered()){
                m_currentRequest.cancel();
            }

            LatLngAlt cameraPosition = m_map.getCameraPosition().target;

            try {
                query = URLEncoder.encode(query, "utf-8");
            }
            catch (UnsupportedEncodingException ex){
                m_errorHandler.handleError(R.string.yelp_search_error_title, R.string.yelp_error_badly_formatted_query);
            }
            String queryUrl = m_searchUrl + queryParams(query, cameraPosition, m_searchRadiusInMeters);

            m_currentRequest = new VolleyStringRequestBuilder(
                    Request.Method.GET, queryUrl, m_searchResponseListener)
                    .setHeaders(m_authHeaders)
                    .setRetryPolicy(m_timeoutMs, m_maxRetries)
                    .build();

            m_requestQueue.add(m_currentRequest);
        }
        else {
            m_deferredQuery = query;
            m_deferredSearchType = SearchType.SEARCH;
            handleUnauthorisedRequest();
        }
    }

    @Override
    public void getSuggestions(String text) {

        if(isAuthenticated()) {

            if(m_currentRequest != null && !m_currentRequest.hasHadResponseDelivered()){
                m_currentRequest.cancel();
            }

            LatLngAlt cameraPosition = m_map.getCameraPosition().target;

            try {
                text = URLEncoder.encode(text, "utf-8");
            }
            catch (UnsupportedEncodingException ex){
                return;
            }

            String queryUrl = m_autocompleteUrl + queryParams(text, cameraPosition);

            m_currentRequest = new VolleyStringRequestBuilder(
                    Request.Method.GET, queryUrl, m_autocompleteResponseListener)
                    .setRetryPolicy(m_timeoutMs, m_maxRetries)
                    .setHeaders(m_authHeaders).build();

            m_requestQueue.add(m_currentRequest);
        }
        else {
            m_deferredQuery = text;
            m_deferredSearchType = SearchType.AUTOCOMPLETE;
            handleUnauthorisedRequest();
        }
    }

    private String queryParams(String text, LatLngAlt location){
        return "?text=" + text +
                "&latitude=" + location.latitude +
                "&longitude=" + location.longitude;
    }

    private String queryParams(String text, LatLngAlt location, int radiusMeters){
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

        performSuggestionCompletedCallbacks(results);
    }

    private void searchResponseHandler(JSONObject response){
        JSONArray businesses = response.optJSONArray(m_resultsArrayKey);
        SearchResult[] results = new SearchResult[businesses.length()];
        for(int i = 0; i < businesses.length(); ++i){
            JSONObject businessJson = businesses.optJSONObject(i);
            results[i] = new YelpSearchResult(businessJson);
        }

        performSearchCompletedCallbacks(results);
    }

    private void createCallbacks(){

        m_authResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    authenticationResponseHandler(new JSONObject(response));
                }
                catch(JSONException e){
                    m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_auth_invalid_response);
                }
            }
        };

        m_searchResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    searchResponseHandler(new JSONObject(response));
                } catch (JSONException e) {
                    android.util.Log.w("Yelp Search", "Search Response Error: " +  response);
                    performSearchCompletedCallbacks(new SearchResult[0]);
                }
            }
        };

        m_autocompleteResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    suggestionResponseHandler(new JSONObject(response));
                } catch (JSONException e) {
                    android.util.Log.w("Yelp Search", "Suggestion Response Error: " +  response);
                    performSuggestionCompletedCallbacks(new SearchResult[0]);
                }
            }
        };

        m_authErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError){
                    m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_auth_timed_out);
                    m_authStatus = AuthStatus.TIMED_OUT;
                    return;
                }
                if(error instanceof NoConnectionError){
                    m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_no_connection);
                    m_authStatus = AuthStatus.TIMED_OUT;
                    return;
                }
                if(error.networkResponse.statusCode == 400) {
                    m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_auth_rejected);
                }
                if(error.networkResponse.statusCode == 404) {
                    m_errorHandler.handleError(R.string.yelp_auth_error_title, R.string.yelp_error_auth_404);
                }
                m_authStatus = AuthStatus.REJECTED;
            }
        };
    }

    private void sendDeferredQuery(){
        if(m_deferredSearchType == SearchType.SEARCH){
            getSearchResults(m_deferredQuery);
        }
        else{
            getSuggestions(m_deferredQuery);
        }
    }

    private void handleUnauthorisedRequest(){
        switch(m_authStatus){
            case TIMED_OUT:
                authenticate(m_yelpApiClientKey, m_yelpApiClientSecret);
                break;
            case REJECTED:
                if(!m_hasRaisedAuthError){
                    m_errorHandler.handleError(R.string.yelp_search_error_title, R.string.yelp_error_rejected_authorisation_search);
                    m_hasRaisedAuthError = true;
                }
                break;
            case UNAUTHENTICATED:
                if(!m_hasRaisedAuthError) {
                    m_errorHandler.handleError(R.string.yelp_search_error_title, R.string.yelp_error_unauthorised_search);
                    m_hasRaisedAuthError = true;
                }
                break;
        }
    }
}
