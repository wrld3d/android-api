package com.eegeo.mapapi.services.mapscene;

import androidx.annotation.UiThread;

/**
 * The data for a custom search menu option.  Used to define custom search terms for Mapscenes.
 */
public class MapsceneSearchMenuItem
{
    /**
     * The unique display name of this search option.
     */
    public final String name;

    /**
     * The WRLD Search Tag to query as part of this search option.
     */
    public final String tag;

    /**
     * The Icon key that specifies what icon to display for this search option.
     */
    public final String iconKey;

    /**
     * (Example App Only) Optional configuration for the search to skip external search services.
     */
    public final boolean skipYelpSearch;

    @UiThread
    MapsceneSearchMenuItem(
            String name,
            String tag,
            String iconKey,
            boolean skipYelpSearch)
    {
        this.name = name;
        this.tag = tag;
        this.iconKey = iconKey;
        this.skipYelpSearch = skipYelpSearch;
    }
}
