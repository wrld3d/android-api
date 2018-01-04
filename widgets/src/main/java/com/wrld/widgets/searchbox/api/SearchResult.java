package com.wrld.widgets.searchbox.api;

public interface SearchResult {
    String getTitle();

    boolean hasProperty(String propertyKey);
    SearchResultProperty getProperty(String propertyKey);
}
