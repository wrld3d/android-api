package com.wrld.searchproviders;

import com.eegeo.mapapi.geometry.LatLng;
import com.wrld.widgets.searchbox.DefaultSearchResult;
import com.wrld.widgets.searchbox.SearchResultPropertyString;

import org.json.JSONObject;

public class YelpSearchResult extends DefaultSearchResult {

    public static final String RatingKey = "Rating";
    public static final String ReviewCountKey = "ReviewCount";
    public static final String BusinessLink = "BusinessLink";

    private static final String m_defaultUrl = "https://www.yelp.com";

    public YelpSearchResult(JSONObject yelpBusinessJson) {
        super(  yelpBusinessJson.optString("name"));
        JSONObject coordinateJson = yelpBusinessJson.optJSONObject("coordinates");

        addProperty(SearchPropertyLatLng.Key, new SearchPropertyLatLng(
                new LatLng(coordinateJson.optDouble("latitude"), coordinateJson.optDouble("longitude"))));
        addProperty(RatingKey, new SearchResultPropertyDouble(RatingKey, yelpBusinessJson.optDouble("rating", 0)));
        addProperty(ReviewCountKey, new SearchResultPropertyInt(ReviewCountKey, yelpBusinessJson.optInt("review_count", 0)));
        addProperty(BusinessLink, new SearchResultPropertyString(ReviewCountKey, yelpBusinessJson.optString("url", m_defaultUrl)));
    }
}
