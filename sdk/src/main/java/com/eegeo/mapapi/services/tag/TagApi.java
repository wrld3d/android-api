package com.eegeo.mapapi.services.tag;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;
import com.eegeo.mapapi.util.Promise;


public class TagApi
{
    private INativeMessageRunner m_nativeRunner;
    private IUiMessageRunner m_uiRunner;
    private long m_jniEegeoMapApiPtr;
    private Boolean m_hasLoadedTags;
    private Boolean m_loadInProgress;
    private OnTagsLoadCompletedListener m_listener;

    public TagApi(INativeMessageRunner nativeRunner, IUiMessageRunner uiRunner, long jniEegeoMapApiPtr) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;
        this.m_jniEegeoMapApiPtr = jniEegeoMapApiPtr;
        this.m_hasLoadedTags = false;
        this.m_loadInProgress = false;
        this.m_listener = null;
    }


    @UiThread
    public Promise<String> getIconUrlByKey(String iconKey)
    {
        final String iconKeyInput = iconKey;
        final Promise<String> p = new Promise<String>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final String nativeResult = nativeGetIconUrlForIconKey(m_jniEegeoMapApiPtr, iconKeyInput);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(nativeResult);
                    }
                });
            }
        });

        return p;
    }

    @UiThread
    public Promise<String> getIconKeyForTags(String tags)
    {
        final String tagsInput = tags;
        final Promise<String> p = new Promise<String>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final String nativeResult = nativeGetIconKeyForTagsString(m_jniEegeoMapApiPtr, tagsInput);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(nativeResult);
                    }
                });
            }
        });

        return p;
    }

    public Promise<String> getIconUrlForTags(String tags)
    {
        final String tagsInput = tags;
        final Promise<String> p = new Promise<String>();
        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final String nativeResult = nativeGetIconUrlForTagsString(m_jniEegeoMapApiPtr, tagsInput);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(nativeResult);
                    }
                });
            }
        });

        return p;
    }

    public Promise<String[]> getReadableTagsForTags(String tags)
    {
        final String tagsInput = tags;
        final Promise<String[]> p = new Promise<String[]>();

        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                final String[] nativeResult = nativeGetReadableTagsForTagsString(m_jniEegeoMapApiPtr, tagsInput);
                m_uiRunner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        p.ready(nativeResult);
                    }
                });
            }
        });

        return p;
    }

    @UiThread
    public void loadTags(OnTagsLoadCompletedListener listener)
    {
        if(m_loadInProgress)
        {
            throw new UnsupportedOperationException("Unable to load Tags - load already in progress!");
        }

        m_listener = listener;
        m_loadInProgress = true;

        m_nativeRunner.runOnNativeThread(new Runnable() {
            @Override
            public void run() {
                nativeLoadTags(m_jniEegeoMapApiPtr);
            }
        });
    }

    @UiThread
    public Boolean hasLoadedTags()
    {
        return m_hasLoadedTags;
    }

    @WorkerThread
    public void notifyTagsLoaded()
    {
        m_uiRunner.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_hasLoadedTags = true;
                m_loadInProgress = false;
                if(m_listener != null)
                {
                    m_listener.onTagsLoadCompleted();
                }
                m_listener = null;
            }
        });
    }

    private native int nativeLoadTags(long jniEegeoMapApiPtr);

    private native String nativeGetIconUrlForIconKey(long jniEegeoMapApiPtr, String iconKey);

    private native String nativeGetIconKeyForTagsString(long jniEegeoMapApiPtr, String tags);

    private native String nativeGetIconUrlForTagsString(long jniEegeoMapApiPtr, String tags);

    private native String[] nativeGetReadableTagsForTagsString(long jniEegeoMapApiPtr, String tags);


}
