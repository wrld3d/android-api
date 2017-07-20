package com.eegeo.mapapi;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Singleton container for the eeGeo API key and application context.
 *
 */
public final class EegeoApi {
    private static EegeoApi ms_instance;
    private String m_apiKey;

    private EegeoApi(@NonNull String apiKey) {
        this.m_apiKey = apiKey;
    }

    /**
     * Creates an EegeoApi object. This object should only be constructed once.
     *
     * @param context An android.content.Context object, used to obtain the global application context.
     * @param apiKey  The eeGeo API key, obtained from [https://www.eegeo.com/developers/apikeys/](https://www.eegeo.com/developers/apikeys/).
     *                This is a 32 character alphanumeric string which should be specific to each
     *                application.
     * @return The singleton EegeoApi instance.
     */
    public static synchronized EegeoApi init(@NonNull Context context, @NonNull String apiKey) {
        if (ms_instance == null) {
            ms_instance = new EegeoApi(apiKey);
        }
        return ms_instance;
    }

    /**
     * Gets the instance of the class.
     *
     * @return The singleton EegeoApi instance.
     */
    public static synchronized EegeoApi getInstance() {
        assert (ms_instance != null) : "Call EegeoApi.init first";
        return ms_instance;
    }

    /**
     * Gets the eeGeo API key.
     *
     * @return The eeGeo API key used to initialize the object.
     */
    public String getApiKey() {
        return m_apiKey;
    }

}
