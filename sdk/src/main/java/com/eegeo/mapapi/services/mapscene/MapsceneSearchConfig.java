package com.eegeo.mapapi.services.mapscene;

import androidx.annotation.UiThread;

import java.util.List;

/**
 * The optional configuration to apply to the WRLD Searchbox Widget for a given Mapscene.
 */
public class MapsceneSearchConfig
{
    /**
     * The list of custom Searches defined in the Searchbox Widget's Find menu when viewing the
     * outdoor map.
     */
    public final List<MapsceneSearchMenuItem> outdoorSearchMenuItems;

    /**
     * Optional flag to specify an initial search to perform when Mapscene is loaded.
     */
    public final boolean performStartupSearch;

    /**
     * Optional search term to execute if the 'performStartupSearch' option is set.
     */
    public final String startupSearchTerm;

    /**
     * Option to use the outdoorSearchMenuItems configuration even in Indoor Maps that have their
     * own set of defined Search Menu items.
     */
    public final boolean overrideIndoorSearchMenu;

    @UiThread
    MapsceneSearchConfig(List<MapsceneSearchMenuItem> outdoorSearchMenuItems,
                         boolean performStartupSearch,
                         String startupSearchTerm,
                         boolean overrideIndoorSearchMenu)
    {
        this.outdoorSearchMenuItems = outdoorSearchMenuItems;
        this.performStartupSearch = performStartupSearch;
        this.startupSearchTerm = startupSearchTerm;
        this.overrideIndoorSearchMenu = overrideIndoorSearchMenu;
    }


}
