package com.eegeo.mapapi.services.poi;

import android.support.annotation.UiThread;


public interface OnPoiSearchCompletedListener {

    @UiThread
    void onPoiSearchCompleted(PoiSearchResults response);

}
