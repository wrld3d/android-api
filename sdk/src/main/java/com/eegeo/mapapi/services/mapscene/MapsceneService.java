package com.eegeo.mapapi.services.mapscene;

import android.support.annotation.UiThread;

import com.eegeo.mapapi.services.poi.PoiSearch;
import com.eegeo.mapapi.services.poi.TextSearchOptions;

/**
 * A service which allows you to request Mapscenes, as created by the WRLD Map Designer.
 * Created by the createMapsceneService method of the EegeoMap object.
 *
 * This is a Java interface to the WRLD MAPSCENE REST API (https://github.com/wrld3d/wrld-mapscene-api).
 *
 * It also supports additional options for applying a Mapscene to a map when you successfully load it.
 */
public class MapsceneService {

    private MapsceneApi m_mapsceneApi;

    /**
     * @eegeo.internal
     */
    public MapsceneService(MapsceneApi mapsceneApi)
    {
        this.m_mapsceneApi = mapsceneApi;
    }

    /**
     * Begins a Mapscene request with the given options.
     *
     * The results of the request will be passed as a MapsceneRequestResponse object to the callback provided in the options.
     *
     * @param options The parameters of the request.
     * @return A handle to the ongoing request, which can be used to cancel it.
     */
    @UiThread
    public MapsceneRequest requestMapscene(final MapsceneRequestOptions options) {
        return m_mapsceneApi.requestMapscene(options);
    }
}
