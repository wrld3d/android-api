package com.wrld.widgets.searchbox;

import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.wrld.widgets.R;
import java.util.ArrayList;

class SearchBoxController {

    private SearchView m_view;

    SearchBoxController(ViewGroup searchboxRootContainer) {
        m_view = (SearchView) searchboxRootContainer.findViewById(R.id.search_box);
        m_onSearchResultsReceivedCallbacks = new ArrayList<OnSearchResultsReceivedCallback>();

        m_view.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText != null && !TextUtils.isEmpty(newText.trim())) {
                        // Limited (Auto) Query call
                        for(OnSearchResultsReceivedCallback callback : m_onSearchResultsReceivedCallbacks){

                            // Full Query call + More stuff (POIs, Close keypad )
                            callback.performSearch(newText);
                        }
                    }
                    else {
                        //TODO: Clear Results
                        android.util.Log.v("SA TextChange: ", "Empty String");
                    }

                    android.util.Log.v("SA TextChange: ",newText);
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    for(OnSearchResultsReceivedCallback callback : m_onSearchResultsReceivedCallbacks){

                        // Full Query call + More stuff (POIs, Close keypad )
                        callback.performSearch(query);
                    }
                    return true;
                }
            }
        );
    }

    /**
     * Defines the signature for a method that is called when the EegeoMap object has been created and
     * is ready to call.
     */
    public interface OnSearchResultsReceivedCallback {
        /**
         * Called when search results are returned from a SearchProvider
         ***/
        @UiThread
        void performSearch(String query);
    }

    public ArrayList<OnSearchResultsReceivedCallback> m_onSearchResultsReceivedCallbacks;

    public void addSearchResultSubmissionCallback(OnSearchResultsReceivedCallback callback) {
        m_onSearchResultsReceivedCallbacks.add(callback);
    }







}
