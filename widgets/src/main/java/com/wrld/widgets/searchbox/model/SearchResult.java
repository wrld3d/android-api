package com.wrld.widgets.searchbox.model;

public interface SearchResult {
    String getTitle();

    boolean hasProperty(String propertyKey);
    SearchResultProperty getProperty(String propertyKey);

    void select();
}

