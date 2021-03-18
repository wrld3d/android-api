package com.eegeo.mapapi.services.poi;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;


/**
 * A service which allows you to search for POIs. Created by the createPoiService method of the EegeoMap object.
 *
 * This is a Java interface to the <a href="https://github.com/wrld3d/wrld-poi-api">WRLD POI REST API</a>
 *
 * It currently supports three different kinds of search: free-text search, tag search, and autocomplete search.
 */
public class PoiService {

    private PoiApi m_poiApi;

    /**
     * @eegeo.internal
     */
    public PoiService(PoiApi poiApi) {
        this.m_poiApi = poiApi;
    }

    /**
     * Begins a free-text search for POIs with the given query options.
     *
     * The results of the search will be passed as a PoiSearchResponse object to the callback provided in the options.
     *
     * @param options The parameters of the search.
     * @return A handle to the ongoing search, which can be used to cancel it.
     */
    @UiThread
    public PoiSearch searchText(final TextSearchOptions options) {
        return m_poiApi.searchText(options);
    }


    /**
     * Begins a tag search for POIs with the given query options.
     *
     * The results of the search will be passed as a PoiSearchResponse object to the callback provided in the options.
     *
     * @param options The parameters of the search.
     * @return A handle to the ongoing search, which can be used to cancel it.
     */
    @UiThread
    public PoiSearch searchTag(final TagSearchOptions options) {
        return m_poiApi.searchTag(options);
    }


    /**
     * Begins an autocomplete search for POIs with the given query options.
     *
     * The results of the search will be passed as a PoiSearchResponse object to the callback provided in the options.
     *
     * @param options The parameters of the search.
     * @return A handle to the ongoing search, which can be used to cancel it.
     */
    @UiThread
    public PoiSearch searchAutocomplete(final AutocompleteOptions options) {
        return m_poiApi.searchAutocomplete(options);
    }
}

