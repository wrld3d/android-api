package com.eegeo.mapapi.services.tag;

import androidx.annotation.UiThread;

/**
 * A listener interface for receiving the results of a Load Tags request
 *
 * An object implementing this should be passed to the LoadTags() method when you want a callback notification.
 */
public interface OnTagsLoadCompletedListener {

    /**
     * Called when the Tags data is loaded and ready to be queried by the TagService.
     */
    @UiThread
    void onTagsLoadCompleted();
}
