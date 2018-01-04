package com.wrld.widgets.searchbox;

import android.view.ViewGroup;

public class SearchModuleFactory {
    public static com.wrld.widgets.searchbox.api.SearchModule create(ViewGroup appSearchAreaView){
        SearchModule searchModule =  new SearchModule();
        searchModule.inflateViewsAndAssignControllers(appSearchAreaView);
        return searchModule;
    }
}
