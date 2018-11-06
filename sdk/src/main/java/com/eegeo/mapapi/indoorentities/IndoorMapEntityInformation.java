package com.eegeo.mapapi.indoorentities;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.util.NativeApiObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 */
public class IndoorMapEntityInformation extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final IndoorMapEntityInformationApi m_indoorMapEntityInformationApi;

    private final String m_indoorMapId;
    private final List<IndoorMapEntity> m_indoorMapEntities;
    private final IndoorMapEntityLoadState m_loadState;


    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorMapEntityInformation(@NonNull final IndoorMapEntityInformationApi indoorMapEntityInformationApi,
                                      @NonNull final String indoorMapId)
    {
        super(indoorMapEntityInformationApi.getNativeRunner(),
                indoorMapEntityInformationApi.getUiRunner(),
                new Callable<Integer>()
                {
                    @Override
                    public Integer call() throws Exception {
                        return indoorMapEntityInformationApi.create(indoorMapId, m_allowHandleAccess);
                    }
                });

        m_indoorMapEntityInformationApi = indoorMapEntityInformationApi;

        m_indoorMapId = indoorMapId;
        m_indoorMapEntities = new ArrayList<>();
        m_loadState = IndoorMapEntityLoadState.None;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_indoorMapEntityInformationApi.register(IndoorMapEntityInformation.this, m_allowHandleAccess);
            }
        });
    }

    public String getIndoorMapId() {return m_indoorMapId;}

    public List<IndoorMapEntity> getIndoorMapEntities() {return m_indoorMapEntities;}

    public IndoorMapEntityLoadState getLoadState() {return  m_loadState;}

    @UiThread
    public void destroy() {
        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_indoorMapEntityInformationApi.destroy(IndoorMapEntityInformation.this, IndoorMapEntityInformation.m_allowHandleAccess);
            }
        });

    }

    @UiThread
    void setIndoorEntityInformation(IndoorMapEntityInformation indoorMapEntityInformation)
    {

    }

    @WorkerThread
    int getNativeHandle(AllowHandleAccess allowHandleAccess) {
        if (allowHandleAccess == null)
            throw new NullPointerException("Null access token. Method is intended for use by BuildingsApi");

        if (!hasNativeHandle())
            throw new IllegalStateException("Native handle not available");

        return getNativeHandle();
    }

    static final class AllowHandleAccess {
        @WorkerThread
        private AllowHandleAccess() {
        }
    }
}
