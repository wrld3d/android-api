package com.eegeo.mapapi.services.tag;

import androidx.annotation.UiThread;

import com.eegeo.mapapi.util.Promise;

/**
 * A service which allows you to resolve PoiSearchResult.tags to matching icons and descriptions.
 *
 * The search tag data is defined in https://github.com/wrld3d/wrld-icon-tools/blob/master/data/search_tags.json,
 * and this Service will load and internally process that manifest to create a table that lets you resolve tags
 * to icons and descriptors.
 */
public class TagService {

    private TagApi m_tagApi;

    /**
     * @eegeo.internal
     */
    public TagService(TagApi tagApi)
    {
        this.m_tagApi = tagApi;
    }

    /**
     * Notify the API to load the Search Tags data. Until this completes, most methods on this service
     * will just return defaults.  You'll be notified when the load completes via the OnTagsLoadCompletedListener,
     * after which time you can start calling the other methods to resolve Tags
     * @param listener an optional listener that will be called when the load is completed
     */
    @UiThread
    public void loadTags(OnTagsLoadCompletedListener listener)
    {
        m_tagApi.loadTags(listener);
    }

    /**
     * Have we successfully finished loading the Search Tags data?
     * @return true on successful load of Search Tags data. This should be true after a successful
     * call to the OnTagsLoadCompletedListener
     */
    @UiThread
    public Boolean hasLoadedTags()
    {
        return m_tagApi.hasLoadedTags();
    }
    /**
     * Resolve a set of tags from a Poi result to an Icon Key. Icon Keys can then be used by the
     * Markers API or used to resolve an Icon Image URL.
     * @param tags the set of tags i.e. from a PoiSearchResult.tags to find a matching icon for
     * @return a Promise of a resolved Icon Key
     */
    @UiThread
    public Promise<String> getIconKeyForTags(String tags) {
        return m_tagApi.getIconKeyForTags(tags);
    }

    /**
     * Resolve a set of tags from a Poi result to an Icon Image URL.
     * @param tags the set of tags i.e. from a PoiSearchResult.tags to find a matching icon for
     * @return a Promise of a url matching the resolved Icon Key for an image
     */
    @UiThread
    public Promise<String> getIconUrlForTags(String tags) {
        return m_tagApi.getIconUrlForTags(tags);
    }

    /**
     * Resolve an Icon Key to an Icon Image URL directly.
     * @param iconKey the Icon Key to find a matching Icon Image URL for.
     * @return a Promise of a URL matching the resolved Icon Key for an image
     */
    @UiThread
    public Promise<String> getIconUrlForIconKey(String iconKey) {
        return m_tagApi.getIconUrlByKey(iconKey);
    }

    /**
     * Resolve a set of tags to a collection of Human Readable tag descriptions. i.e. "sports" converts to
     * "Sports and Leisure".
     * @param tags the set of tags i.e. from a PoiSearchResult.tags to find matching tag descriptions for.
     * @return a Promise of a set of human readable descriptions in a String array, matching the input order.
     */
    @UiThread
    public Promise<String[]> getReadableTagsForTags(String tags) {
        return m_tagApi.getReadableTagsForTags(tags);
    }
}
