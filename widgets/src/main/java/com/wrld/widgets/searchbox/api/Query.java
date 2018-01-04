package com.wrld.widgets.searchbox.api;

public interface Query {
    String getQueryString();
    boolean hasCompleted();
    boolean wasCancelled();
    void cancel();
}
