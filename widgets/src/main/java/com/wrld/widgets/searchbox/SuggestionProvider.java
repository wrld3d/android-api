// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

public interface SuggestionProvider extends SearchProvider {
    void getSuggestions(String text);

    void addOnSuggestionsRecievedCallback(OnResultsReceivedCallback callback);

    void setSuggestionViewFactory(SearchResultViewFactory factory);
    SearchResultViewFactory getSuggestionViewFactory();
}
