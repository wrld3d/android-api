package com.wrld.widgets.searchbox.api;

public interface SearchResultProperty<T> {
    String getKey();
    T getValue();

    int compareTo(SearchResultProperty<T> other);
}
