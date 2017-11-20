package com.eegeo.mapapi.services.mapscene;


import android.support.annotation.UiThread;

/**
 * The data defining the source manifest files to load for a given Mapscene.  This defines what
 * data is loaded and what theme to apply.
 */
public class MapsceneDataSources
{
    /**
     * The coverage tree manifest to load for this Mapscene.
     */
    public final String coverageTreeManifestUrl;

    /**
     * The themes manifest to load for this Mapscene.
     */
    public final String themeManifestUrl;

    @UiThread
    MapsceneDataSources(String coverageTreeManifestUrl,
                        String themeManifestUrl)
    {
        this.coverageTreeManifestUrl = coverageTreeManifestUrl;
        this.themeManifestUrl = themeManifestUrl;
    }
}
