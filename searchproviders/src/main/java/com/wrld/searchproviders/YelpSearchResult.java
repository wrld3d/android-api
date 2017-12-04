// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

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

        m_additionalProperties.put(SearchPropertyLatLng.Key, new SearchPropertyLatLng(
                new LatLng(coordinateJson.optDouble("latitude"), coordinateJson.optDouble("longitude"))));
        m_additionalProperties.put(RatingKey, new SearchResultPropertyDouble(RatingKey, yelpBusinessJson.optDouble("rating", 0)));
        m_additionalProperties.put(ReviewCountKey, new SearchResultPropertyInt(ReviewCountKey, yelpBusinessJson.optInt("review_count", 0)));
        m_additionalProperties.put(BusinessLink, new SearchResultPropertyString(ReviewCountKey, yelpBusinessJson.optString("url", m_defaultUrl)));
    }
}
