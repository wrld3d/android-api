package com.wrld.searchproviders;

import com.eegeo.mapapi.geometry.LatLng;
import com.wrld.widgets.searchbox.api.DefaultSearchResult;
import com.wrld.widgets.searchbox.api.SearchResultPropertyString;

import org.json.JSONObject;

public class YelpSearchResult extends DefaultSearchResult {

    public static final String AddressKey = "Address";
    public static final String RatingKey = "Rating";
    public static final String ReviewCountKey = "ReviewCount";
    public static final String BusinessLink = "BusinessLink";

    private static final String m_defaultUrl = "https://www.yelp.com";

    public YelpSearchResult(JSONObject yelpBusinessJson) {
        super(  yelpBusinessJson.optString("name"));
        JSONObject coordinateJson = yelpBusinessJson.optJSONObject("coordinates");
        JSONObject locationJson = yelpBusinessJson.optJSONObject("location");

        addProperty(SearchPropertyLatLng.Key, new SearchPropertyLatLng(
                new LatLng(coordinateJson.optDouble("latitude"), coordinateJson.optDouble("longitude"))));
        addProperty(AddressKey, new SearchResultPropertyString(AddressKey, locationJson.optString("address1", "")));
        addProperty(RatingKey, new SearchResultPropertyDouble(RatingKey, yelpBusinessJson.optDouble("rating", 0)));
        addProperty(ReviewCountKey, new SearchResultPropertyInt(ReviewCountKey, yelpBusinessJson.optInt("review_count", 0)));
        addProperty(BusinessLink, new SearchResultPropertyString(ReviewCountKey, yelpBusinessJson.optString("url", m_defaultUrl)));
    }
}
