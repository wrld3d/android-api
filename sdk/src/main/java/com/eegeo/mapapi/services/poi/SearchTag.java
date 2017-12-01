package com.eegeo.mapapi.services.poi;

import android.support.annotation.UiThread;

/**
 * A SearchTag definition maps a specific tag ID to a human readable string and the icon ID for
 * referencing what visual icon to use to represent matching search results.
 */
public class SearchTag
{
    /**
     * The tag identifier
     */
    public final String tag;

    /**
     * The human readable string for the tag, currently English only
     */
    public final String readableTag;

    /**
     * The icon resource identifier used to resolve a path to an icon resource for representing
     * this tag
     */
    public final String iconKey;

    @UiThread
    public SearchTag(String tag, String readableTag, String iconKey)
    {
        this.tag = tag;
        this.readableTag = readableTag;
        this.iconKey = iconKey;
    }


}
