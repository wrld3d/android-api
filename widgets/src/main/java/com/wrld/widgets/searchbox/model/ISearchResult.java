package com.wrld.widgets.searchbox.model;

public interface ISearchResult {
    String getTitle();

    boolean hasProperty(String propertyKey);
    SearchResultProperty getProperty(String propertyKey);

    void select();
}

