package com.eegeo.mapapi.services.poi;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;

import java.util.HashMap;
import java.util.List;


public class TagsApi
{
    // TODO: Move this into platform config and pass back via SearchTags.
    private final String m_baseUrl="https://cdn1.wrld3d.com/wrld-search/latest/assets/android/res/";

    private SearchTags m_searchTags = null;
    private HashMap<String, SearchTag> m_searchTagMap;

    private final HashMap<String, String> m_resolutionSuffixPathMap;
    private String m_resolutionSuffixPath = "drawable-mdpi";
    private final String m_defaultReadable = "Unknown";
    private final String m_defaultIconKey = "pin";

    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;

    public TagsApi(INativeMessageRunner nativeRunner, IUiMessageRunner uiRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
        m_resolutionSuffixPathMap = new HashMap<String, String>();
        m_resolutionSuffixPathMap.put("@0.75x", "drawable-ldpi");
        m_resolutionSuffixPathMap.put("",       "drawable-mdpi");
        m_resolutionSuffixPathMap.put("@1.5x",  "drawable-hdpi");
        m_resolutionSuffixPathMap.put("@2x", "   drawable-xhdpi");
        m_resolutionSuffixPathMap.put("@3x",    "drawable-xxhdpi");
        m_searchTagMap = new HashMap<>();
    }

    @UiThread
    public Boolean hasTag(String searchTag)
    {
        return m_searchTagMap.containsKey(searchTag);
    }

    @UiThread
    public SearchTag findSearchTagByTag(String searchTag)
    {
        return hasTag(searchTag) ? m_searchTagMap.get(searchTag) : null;
    }

    @UiThread
    public String findIconUrlByKey(String iconKey)
    {
        return String.format("{0}{1}/icon1.{2}.png",
                m_baseUrl,
                m_resolutionSuffixPath,
                iconKey
                );
    }

    @UiThread
    public String getDefaultIconKey()
    {
        return m_searchTags == null ? m_defaultIconKey : m_searchTags.defaultIconKey;
    }

    @UiThread
    public String findHumanReadableBySearchTag(String searchTag)
    {
        if(hasTag(searchTag))
        {
            return findSearchTagByTag(searchTag).readableTag;
        }
        else if(m_searchTags != null)
        {
            return m_searchTags.defaultReadableTag;
        }
        else return m_defaultReadable;
    }

    @UiThread
    public String[] findHumanReadablesByTagsString(String tagsString)
    {
        String[] searchTags = tagsString.split(" ");
        String[] result = new String[searchTags.length];
        int i = 0;
        for(String tag : searchTags)
        {
            result[i] = findHumanReadableBySearchTag(tag);
        }

        return result;
    }

    @UiThread
    public String findIconKeyByTagsString(String tagsString)
    {
        String[] searchTags = tagsString.split(" ");
        String result = getDefaultIconKey();
        int i = 0;
        for(String tag : searchTags)
        {
            if(hasTag(tag))
            {
                result = findIconKeyByTag(tag);
                break;
            }
        }

        return result;
    }

    @UiThread
    public String findIconKeyByTag(String searchTag)
    {
        return hasTag(searchTag) ? findSearchTagByTag(searchTag).iconKey : getDefaultIconKey();
    }

    @WorkerThread
    private void loadTags()
    {
        nativeLoadTags(m_jniEegeoMapApiPtr);
    }

    @WorkerThread
    public void notifyTagsLoaded(SearchTags searchTags)
    {
        final SearchTags localTags = searchTags;

        m_uiRunner.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            m_searchTags = localTags;
            buildSearchTagMap(m_searchTags.tags, m_searchTagMap);
            }
        });
    }

    @UiThread
    private void buildSearchTagMap(final List<SearchTag> searchTags, HashMap<String, SearchTag> refMap)
    {
        refMap.clear();
        for(SearchTag tag : searchTags)
        {
            refMap.put(tag.tag, tag);
        }
    }

    private native int nativeLoadTags(long jniEegeoMapApiPtr);
}
