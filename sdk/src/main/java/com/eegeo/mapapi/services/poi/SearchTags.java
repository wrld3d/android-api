package com.eegeo.mapapi.services.poi;

import java.util.List;

/**
 * A set of definitions for mapping tags applied to Search Results with mappable descriptions and icons.
 */
public class SearchTags
{
    /**
     * The list of identifiable search tags and their mapped icons and descriptions
     */
    public final List<SearchTag> tags;

    /**
     * The default description of an unknown search result when no tags can be identified
     */
    public final String defaultReadableTag;

    /**
     * The default icon identifier to use for an unknown search result when no tags can be identified
     */
    public final String defaultIconKey;

    /**
     * The suffix used to define what resolution of an icon asset to load for this device i.e. "@2x"
     */
    public final String imageResolutionSuffix;

    public SearchTags(List<SearchTag> tags,
                      String defaultReadableTag,
                      String defaultIconKey,
                      String imageResolutionSuffix)
    {
        this.tags = tags;
        this.defaultReadableTag = defaultReadableTag;
        this.defaultIconKey = defaultIconKey;
        this.imageResolutionSuffix = imageResolutionSuffix;
    }
}
