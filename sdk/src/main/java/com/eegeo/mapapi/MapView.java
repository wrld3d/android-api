package com.eegeo.mapapi;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.eegeo.mapapi.map.EegeoMapOptions;
import com.eegeo.mapapi.map.OnMapReadyCallback;
import com.eegeo.mapapi.util.Callbacks;

/**
 * View which displays a 3D map.
 */
public class MapView extends FrameLayout {
    private EegeoNativeMapView m_eegeoNativeMapView;
    private MapViewTouchHandler m_mapViewTouchHandler;
    private Callbacks.CallbackCollection1<EegeoMap> m_onReadyCallbacks = new Callbacks.CallbackCollection1<EegeoMap>();
    private boolean m_ready = false;
    private EegeoMap m_eegeoMap = null;


    /**
     * Constructor for a MapView object.
     *
     * @param context The Android context for the view.
     */
    @UiThread
    public MapView(@NonNull Context context) {
        super(context);
        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.eegeo_mapview_preview, this);
        } else {
            EegeoMapOptions eegeoMapOptions = EegeoMapOptions.createFromAttributeSet(context, null);
            initialise(context, eegeoMapOptions);
        }
    }

    /**
     * Constructor for a MapView object.
     *
     * @param context      The Android context for the view.
     * @param attributeSet Attributes which may be used to initialize the view.  Attributes may be defined
     *                     in the view's layout xml by setting eegeo_MapView styleable resources.
     */
    @UiThread
    public MapView(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.eegeo_mapview_preview, this);
        } else {
            EegeoMapOptions eegeoMapOptions = EegeoMapOptions.createFromAttributeSet(context, attributeSet);
            initialise(context, eegeoMapOptions);
        }
    }

    /**
     * Constructor for a MapView object.
     *
     * @param context         The Android context for the view.
     * @param eegeoMapOptions An EegeoMapOptions object specifying initial conditions for the object.
     */
    @UiThread
    public MapView(@NonNull Context context, @Nullable EegeoMapOptions eegeoMapOptions) {
        super(context);
        if (eegeoMapOptions == null) {
            eegeoMapOptions = EegeoMapOptions.createFromAttributeSet(context, null);
        }
        initialise(context, eegeoMapOptions);
    }

    @UiThread
    private void initialise(@NonNull Context context, @NonNull final EegeoMapOptions eegeoMapOptions) {
        View view = LayoutInflater.from(context).inflate(R.layout.eegeo_mapview_internal, this);
        setClickable(true);
        setFocusable(true);

        m_eegeoNativeMapView = new EegeoNativeMapView(this, eegeoMapOptions);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolderCallback());
        surfaceView.setVisibility(View.VISIBLE);

        m_mapViewTouchHandler = new MapViewTouchHandler(m_eegeoNativeMapView);
    }

    /**
     * Call this method from the parent Activity's onCreate method.
     *
     * @param savedInstanceState The savedInstanceState from the parent.
     */
    @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // todo pass access key through to native map view

        // todo init egl here
    }

    @UiThread
    void notifyReady(EegeoMap eegeoMap) {
        m_ready = true;
        m_eegeoMap = eegeoMap;
        m_onReadyCallbacks.onCallback(m_eegeoMap);
    }

    /**
     * Sets an object which will be notified when the EegeoMap is available and ready
     * to be used.  This is the expected means to obtain an EegeoMap object.
     *
     * @param callback The object to be notified.
     */
    @UiThread
    public void getMapAsync(final OnMapReadyCallback callback) {
        if (m_ready) {
            callback.onMapReady(m_eegeoMap);
        } else {
            m_onReadyCallbacks.add(new OnMapReadyCallbackImpl(callback));
        }
    }

    /**
     * Call this method from the parent Activity's onResume method.
     */
    @UiThread
    public void onResume() {
        m_eegeoNativeMapView.onResume();
    }

    /**
     * Call this method from the parent Activity's onPause method.
     */
    @UiThread
    public void onPause() {
        m_eegeoNativeMapView.onPause();
    }

    /**
     * Call this method from the parent Activity's onDestroy method.
     */
    @UiThread
    public void onDestroy() {
        m_eegeoNativeMapView.onDestroy();
    }

    /**
     * Delegates to the native code to draw the view.
     *
     * @param canvas Android canvas on which the view is drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        m_eegeoNativeMapView.draw();
    }

    /**
     * Called when the user touches the screen.
     *
     * @param e The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return m_mapViewTouchHandler.onTouchEvent(e) || super.onTouchEvent(e);
    }

    private class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            m_eegeoNativeMapView.surfaceCreated(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            m_eegeoNativeMapView.surfaceDestroyed(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            m_eegeoNativeMapView.surfaceChanged(holder, format, width, height);
        }
    }

    private class OnMapReadyCallbackImpl implements Callbacks.ICallback1<EegeoMap> {
        private final OnMapReadyCallback m_callback;

        public OnMapReadyCallbackImpl(OnMapReadyCallback callback) {
            this.m_callback = callback;
        }

        public void onCallback(EegeoMap eegeoMap) {
            m_callback.onMapReady(eegeoMap);
        }
    }
}
