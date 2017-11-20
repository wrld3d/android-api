package com.eegeo.mapapi.services.mapscene;

import android.support.annotation.UiThread;

/**
 * The data that defines a Mapscene, as created by the Map Designer or the Mapscene REST Service.
 */
public class Mapscene
{
    /**
     * The name of the Mapscene
     */
    public final String name;

    /**
     * The hashed ID of the Mapscene, used when loading this Mapscene
     */
    public final String shortlink;

    /**
     * The API Key to use for authenticating with the WRLD SDK. This is also used to link
     * associated POI sets for use with the Searchbox Widget + POI Api.
     */
    public final String apiKey;

    /**
     * Configuration of the initial start location of this Mapscene.
     */
    public final MapsceneStartLocation startLocation;

    /**
     * Configuration of the data and themes to load for this Mapscene.
     */
    public final MapsceneDataSources dataSources;

    /**
     * Optional configuration of the Searchbox Widget for this Mapscene.
     */
    public final MapsceneSearchConfig searchConfig;

    @UiThread
    Mapscene(String name,
             String shortLink,
             String apiKey,
             MapsceneStartLocation startLocation,
             MapsceneDataSources dataSources,
             MapsceneSearchConfig searchConfig)
    {
        this.name = name;
        this.shortlink = shortLink;
        this.apiKey = apiKey;
        this.startLocation = startLocation;
        this.dataSources = dataSources;
        this.searchConfig = searchConfig;
    }
}
