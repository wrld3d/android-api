package com.wrld.widgets.searchbox;

public interface SearchResultProperty<T> {
    String getKey();
    T getValue();

    int compareTo(SearchResultProperty<T> other);
}
