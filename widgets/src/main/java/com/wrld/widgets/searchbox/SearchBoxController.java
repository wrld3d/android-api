package com.wrld.widgets.searchbox;

import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.wrld.widgets.R;
import java.util.ArrayList;

class SearchBoxController {

    private SearchView m_view;

    public ArrayList<OnSearchQueryUpdatedCallback> m_onQueryUpdatedCallbacks;
    public ArrayList<OnSearchQueryUpdatedCallback> m_onQuerySubmitedCallbacks;

    SearchBoxController(ViewGroup searchboxRootContainer) {
        m_view = (SearchView) searchboxRootContainer.findViewById(R.id.searchbox_search_querybox);
        m_onQueryUpdatedCallbacks = new ArrayList<OnSearchQueryUpdatedCallback>();
        m_onQuerySubmitedCallbacks = new ArrayList<OnSearchQueryUpdatedCallback>();

        m_view.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText != null && !TextUtils.isEmpty(newText.trim())) {
                        // Limited (Auto) Query call
                        for(OnSearchQueryUpdatedCallback callback : m_onQueryUpdatedCallbacks){

                            // Full Query call + More stuff (POIs, Close keypad )
                            callback.performQuery(newText);
                        }
                    }
                    else {
                        //TODO: Clear Results
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    for(OnSearchQueryUpdatedCallback callback : m_onQuerySubmitedCallbacks){

                        // Full Query call + More stuff (POIs, Close keypad )
                        callback.performQuery(query);
                    }
                    return true;
                }
            }
        );
    }

    public void performQuery(){
        performQuery(m_view.getQuery());
    }

    public void performQuery(CharSequence query){
        m_view.setQuery(query, true);
    }

    /**
     * Defines the signature for a method that is called when the EegeoMap object has been created and
     * is ready to call.
     */
    public interface OnSearchQueryUpdatedCallback {
        /**
         * Called when search results are returned from a SearchProvider
         ***/
        @UiThread
        void performQuery(String query);
    }


    public void addQueryUpdatedCallback(OnSearchQueryUpdatedCallback callback) {
        m_onQueryUpdatedCallbacks.add(callback);
    }

    public void addQuerySubmittedCallback(OnSearchQueryUpdatedCallback callback) {
        m_onQuerySubmitedCallbacks.add(callback);
    }







}
