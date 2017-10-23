// Copyright eeGeo Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

public interface SearchResultProperty<T> {
    String getKey();
    T getValue();

    int compareTo(SearchResultProperty<T> other);
}
