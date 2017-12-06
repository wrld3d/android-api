package com.wrld.widgets.searchbox;

public interface SearchResult {
    String getTitle();

    boolean hasProperty(String propertyKey);
    SearchResultProperty getProperty(String propertyKey);
}
