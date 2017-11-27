package com.eegeo.mapapi.services.mapscene;

/**
 * A set of parameters for a Mapscene request
 */
public final class MapsceneRequestOptions {

    private String m_urlOrShortlink;
    private boolean m_applyOnLoad;

    private OnMapsceneRequestCompletedListener m_onMapsceneRequestCompletedListener = null;


    /**
     * @param urlOrShortlink The full url or the shortlink of the Mapscene to load
     */
    public MapsceneRequestOptions(String urlOrShortlink) {
        this.m_urlOrShortlink = urlOrShortlink;
        this.m_applyOnLoad = true;
    }

    /**
     * Sets the option to apply the Mapscene to the map on a successful request. This applies
     * source data, themes, starting location and API Keys to underlying services. Default is 'true'.
     * @param applyOnLoad
     * @return This MapsceneRequestOptions object.
     */
    public MapsceneRequestOptions applyOnLoad(boolean applyOnLoad)
    {
        this.m_applyOnLoad = applyOnLoad;
        return this;
    }


    /**
     * Sets a listener to receive the Mapscene request result when the request completes.
     *
     * @param onMapsceneRequestCompletedListener A listener implementing the OnMapsceneRequestCompletedListener interface.
     * @return This MapsceneRequestOptions object.
     */
    public MapsceneRequestOptions onMapsceneRequestCompletedListener(OnMapsceneRequestCompletedListener onMapsceneRequestCompletedListener) {
        this.m_onMapsceneRequestCompletedListener = onMapsceneRequestCompletedListener;
        return this;
    }


    String getUrlOrShortlink() {
        return m_urlOrShortlink;
    }

    boolean getApplyOnLoad() {
        return m_applyOnLoad;
    }

    OnMapsceneRequestCompletedListener getOnMapsceneRequestCompletedListener() {
        return m_onMapsceneRequestCompletedListener;
    }
}


