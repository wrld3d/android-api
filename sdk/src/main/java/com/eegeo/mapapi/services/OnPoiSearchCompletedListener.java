package com.eegeo.mapapi.services;

import android.support.annotation.UiThread;


public interface OnPoiSearchCompletedListener {

    @UiThread
    void onPoiSearchCompleted(PoiSearchResult response);

}
