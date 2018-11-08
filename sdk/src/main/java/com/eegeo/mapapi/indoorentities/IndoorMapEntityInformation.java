package com.eegeo.mapapi.indoorentities;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.eegeo.mapapi.util.NativeApiObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *  Maintains information about indoor map entities belonging to an indoor map with specified id.
 *  Entity information is updated as map tiles stream in. Change notification is available via
 *  the supplied OnIndoorMapEntityInformationChangedListener.
 */
public class IndoorMapEntityInformation extends NativeApiObject {

    private static final AllowHandleAccess m_allowHandleAccess = new AllowHandleAccess();
    private final IndoorMapEntityInformationApi m_indoorMapEntityInformationApi;

    private final String m_indoorMapId;
    private List<IndoorMapEntity> m_indoorMapEntities;
    private IndoorMapEntityLoadState m_indoorMapEntityLoadState;
    private OnIndoorMapEntityInformationChangedListener m_indoorMapEntityInformationChangedListener;

    /**
     * @eegeo.internal
     */
    @UiThread
    public IndoorMapEntityInformation(
        @NonNull final IndoorMapEntityInformationApi indoorMapEntityInformationApi,
        @NonNull final String indoorMapId,
        final OnIndoorMapEntityInformationChangedListener indoorMapEntityInformationChangedListener
    )
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
        m_indoorMapEntityLoadState = IndoorMapEntityLoadState.None;
        m_indoorMapEntityInformationChangedListener = indoorMapEntityInformationChangedListener;

        submit(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_indoorMapEntityInformationApi.register(IndoorMapEntityInformation.this, m_allowHandleAccess);
            }
        });
    }

    /**
     * Gets the string id of the indoor map associated with this IndoorMapEntityInformation object.
     * @return The indoor map id.
     */
    public String getIndoorMapId() { return m_indoorMapId; }

    /**
     * Gets IndoorMapEntity objects that are currently present.
     * @return The IndoorMapEntity objects currently available for the associated indoor map.
     */
    public List<IndoorMapEntity> getIndoorMapEntities() { return m_indoorMapEntities; }

    /**
     * Gets the current indoor map load state for the associated indoor map.
     * @return The indoor map load state.
     */
    public IndoorMapEntityLoadState getLoadState() { return m_indoorMapEntityLoadState; }


    /**
     * @eegeo.internal
     */
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
    void updateFromNative(IndoorMapEntity indoorMapEntities[], IndoorMapEntityLoadState indoorMapEntityLoadState)
    {
        m_indoorMapEntities = Arrays.asList(indoorMapEntities);
        m_indoorMapEntityLoadState = indoorMapEntityLoadState;
        if (m_indoorMapEntityInformationChangedListener != null) {
            m_indoorMapEntityInformationChangedListener.onIndoorMapEntityInformationChanged(this);
        }
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
