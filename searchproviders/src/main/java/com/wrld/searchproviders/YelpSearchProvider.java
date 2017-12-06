package com.wrld.searchproviders;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.geometry.LatLngAlt;
import com.wrld.widgets.searchbox.DefaultSearchResult;
import com.wrld.widgets.searchbox.SearchResult;
import com.wrld.widgets.searchbox.SuggestionProviderBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class YelpSearchProvider extends SuggestionProviderBase {

    private static final String m_title = "Yelp";

    private static final String m_authUrl = "https://api.yelp.com/oauth2/token";
    private static final String m_searchUrl = "https://api.yelp.com/v3/businesses/search";
    private static final String m_autocompleteUrl = "https://api.yelp.com/v3/autocomplete";

    private static final String m_authTokenKey = "access_token";

    private static final int m_searchRadiusInMeters = 500;
    private static final int m_initialResultCount = 40;

    private EegeoMap m_map;

    private String m_accessToken;

    private boolean m_isAuthenticated;
    public boolean isAuthenticated () { return m_isAuthenticated; }

    private RequestQueue m_requestQueue;

    public YelpSearchProvider(RequestQueue requestQueue, EegeoMap map){
        super(m_title);
        m_isAuthenticated = false;

        m_requestQueue = requestQueue;
        m_map = map;
    }

    public void authenticate(String yelpApiClientKey, String yelpApiClientSecret) {
        //TODO test isAuthenticated after successful authentication
        //TODO test isAuthenticated after failed authentication (invalid key)
        //TODO test isAuthenticated after failed authentication (invalid secret)

        HashMap<String, String> postArgs = new HashMap<String, String>();

        postArgs.put("grant_type", "client_credentials");
        postArgs.put("client_id", yelpApiClientKey);
        postArgs.put("client_secret", yelpApiClientSecret);

        Response.Listener<String> responseHandler = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    authenticationResponseHandler(new JSONObject(response));
                }
                catch(JSONException e){
                    //TODO Handle auth failure
                }
            }
        };

        StringRequest request =  new VolleyStringRequestBuilder(
                Request.Method.POST,
                m_authUrl,
                responseHandler)
                .setParams(postArgs)
                .build();
        m_requestQueue.add(request);
    }

    private void authenticationResponseHandler(JSONObject response)
    {
        if(response.has(m_authTokenKey)) {
            m_accessToken = response.optString(m_authTokenKey);
            m_isAuthenticated = !TextUtils.isEmpty(m_accessToken);
        }
    }

    @Override
    public void getSearchResults(String query) {
        //TODO test getSearchResults before authentication
        //TODO test getSearchResults after valid authentication
        //TODO test getSearchResults after failed authentication
        if(isAuthenticated()) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + m_accessToken);

            LatLngAlt cameraPosition = m_map.getCameraPosition().target;

            Response.Listener<String> responseHandler = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        searchResponseHandler(new JSONObject(response));
                    } catch (JSONException e) {
                        //TODO Handle invalid json
                    }
                }
            };

            StringRequest request = new VolleyStringRequestBuilder(
                    Request.Method.GET,
                    m_searchUrl + queryParams(query, cameraPosition, m_searchRadiusInMeters),
                    responseHandler)
                    .setHeaders(headers).build();

            m_requestQueue.add(request);
        }
    }

    @Override
    public void getSuggestions(String text) {
        //TODO test getSuggestions before authentication
        //TODO test getSuggestions after valid authentication
        //TODO test getSuggestions after failed authentication

        if(isAuthenticated()) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + m_accessToken);

            LatLngAlt cameraPosition = m_map.getCameraPosition().target;

            Response.Listener<String> responseHandler = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        suggestionResponseHandler(new JSONObject(response));
                    } catch (JSONException e) {
                        //TODO Handle invalid json
                    }
                }
            };

            StringRequest request = new VolleyStringRequestBuilder(
                    Request.Method.GET,
                    m_autocompleteUrl + queryParams(text, cameraPosition),
                    responseHandler)
                    .setHeaders(headers).build();

            m_requestQueue.add(request);
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
        JSONArray businesses = response.optJSONArray("businesses");
        SearchResult[] results = new SearchResult[businesses.length()];
        for(int i = 0; i < businesses.length(); ++i){
            JSONObject businessJson = businesses.optJSONObject(i);
            results[i] = new DefaultSearchResult(businessJson.optString("name"));
        }

        performSuggestionCompletedCallbacks(results);
    }

    private void searchResponseHandler(JSONObject response){
        JSONArray businesses = response.optJSONArray("businesses");
        SearchResult[] results = new SearchResult[businesses.length()];
        for(int i = 0; i < businesses.length(); ++i){
            JSONObject businessJson = businesses.optJSONObject(i);
            results[i] = new YelpSearchResult(businessJson);
        }

        performSearchCompletedCallbacks(results);
    }
}
