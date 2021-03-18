package com.eegeo.mapapi;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.eegeo.mapapi.map.EegeoMapOptions;

import java.util.concurrent.Callable;


public class EegeoNativeMapView implements INativeMessageRunner {
    static {
        System.loadLibrary("eegeo-jni");
    }

    final private NativeThread m_nativeThread;
    final private MapView m_mapView;
    private final UiThreadRunner m_uiRunner;
    private SurfaceHolder m_surfaceHolder;
    private long m_jniApiRunnerPtr;
    private EegeoMap m_eeGeoMap = null;
    private Point m_mouseDownPoint = new Point(0, 0);

    private Callable<EegeoMap> m_createEegeoMapNativeCallable;


    @UiThread
    public EegeoNativeMapView(MapView mapView, @NonNull final EegeoMapOptions eegeoMapOptions) throws RuntimeException {

        final Context context = mapView.getContext();

        this.m_uiRunner = new UiThreadRunner(context);
        this.m_mapView = mapView;
        this.m_nativeThread = new NativeThread(eegeoMapOptions.getTargetFrameRate());
        this.m_nativeThread.start();

        this.m_createEegeoMapNativeCallable = new Callable<EegeoMap>() {
            @Override
            public EegeoMap call() throws Exception {
                EegeoMap eeGeoMap = new EegeoMap(EegeoNativeMapView.this, m_uiRunner, new CreateEegeoMapApiImpl(), eegeoMapOptions);
                eeGeoMap.initialise(eegeoMapOptions);

                return eeGeoMap;
            }
        };


        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                DisplayMetrics dm = context.getResources().getSystem().getDisplayMetrics();
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                    m_jniApiRunnerPtr = nativeCreateApiRunner(EegeoNativeMapView.this, context, context.getAssets(), dm.ydpi, dm.densityDpi, packageInfo.versionName, packageInfo.versionCode);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to get PackageInfo");
                }

                if (m_jniApiRunnerPtr == 0) {
                    throw new RuntimeException("Failed to start native code.");
                }
            }
        });
    }

    @UiThread
    public void runOnNativeThread(Runnable runnable) {
        m_nativeThread.postTo(runnable);
    }

    @UiThread
    public void onResume() {
        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                if (m_surfaceHolder != null && m_surfaceHolder.getSurface() != null) {
                    nativeSetSurface(m_jniApiRunnerPtr, m_surfaceHolder.getSurface());

                    nativeResumeApiRunner(m_jniApiRunnerPtr);
                }
                m_nativeThread.startUpdating();
            }
        });
    }

    @UiThread
    public void onPause() {
        Log.d("eegeo-android-sdk", "onPause");

        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_nativeThread.stopUpdating();
                nativePauseApiRunner(m_jniApiRunnerPtr);
            }
        });
    }

    @UiThread
    public void onDestroy() {
        Log.d("eegeo-android-sdk", "onDestroy");

        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                Log.d("eegeo-android-sdk", "Native Thread: onDestroy");
                m_nativeThread.stopUpdating();
                nativeDestroyApiRunner(m_jniApiRunnerPtr);
                m_jniApiRunnerPtr = 0;
                synchronized (m_nativeThread) {
                    m_nativeThread.notifyAll();
                }
            }
        });

        Log.d("eegeo-android-sdk", "begin wait nativeDestroyApiRunner");
        synchronized (m_nativeThread) {
            try {
                m_nativeThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d("eegeo-android-sdk", "end wait nativeDestroyApiRunner");

        m_nativeThread.quit();
    }


    @UiThread
    public void onPointerDown(final int primaryActionIndex, final int primaryActionIdentifier, final int pointerCount, final float[] x, final float y[], final int[] pointerIdentity, final int[] pointerIndex) {
        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                nativeProcessPointerDown(m_jniApiRunnerPtr, primaryActionIndex, primaryActionIdentifier, pointerCount, x, y, pointerIdentity, pointerIndex);
            }
        });
        if (x.length > 0) { // TODO: is this the most appropriate point?
            m_mouseDownPoint.x = (int) x[0];
            m_mouseDownPoint.y = (int) y[0];
        }
    }

    @UiThread
    public void onPointerUp(final int primaryActionIndex, final int primaryActionIdentifier, final int pointerCount, final float[] x, final float y[], final int[] pointerIdentity, final int[] pointerIndex) {
        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                nativeProcessPointerUp(m_jniApiRunnerPtr, primaryActionIndex, primaryActionIdentifier, pointerCount, x, y, pointerIdentity, pointerIndex);
            }
        });
        if (m_eeGeoMap == null) {
            Log.d("eegeo-android-sdk", "skipping input event -- map not ready");
            return;
        }
        if (x.length > 0) { // TODO: remove this and promote to native ITouchController
            Point mouseUpPoint = new Point((int) x[0], (int) y[0]);
            double distSquared = Math.pow(mouseUpPoint.x - m_mouseDownPoint.x, 2) + Math.pow(mouseUpPoint.y - m_mouseDownPoint.y, 2);
            if (distSquared < 25) {
                m_eeGeoMap.onTapped(mouseUpPoint);
            }
        }
    }

    @UiThread
    public void onPointerMove(final int primaryActionIndex, final int primaryActionIdentifier, final int pointerCount, final float[] x, final float y[], final int[] pointerIdentity, final int[] pointerIndex) {
        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                nativeProcessPointerMove(m_jniApiRunnerPtr, primaryActionIndex, primaryActionIdentifier, pointerCount, x, y, pointerIdentity, pointerIndex);
            }
        });

    }


    public void draw() {
    }

    @WorkerThread
    private void notifySurfaceCreated() {
        // platform creation is deferred until surface is available.
        // If resuming from backgrounded activity, may already have m_eeGeoMap instance
        if (m_eeGeoMap == null) {
            try {
                m_eeGeoMap = m_createEegeoMapNativeCallable.call();
                m_createEegeoMapNativeCallable = null;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to create native eeGeo Map object");
            }

            m_uiRunner.runOnUiThread(new Runnable() {
                @UiThread
                @Override
                public void run() {
                    m_mapView.notifyReady(m_eeGeoMap);
                }
            });
        }

        nativeResumeApiRunner(m_jniApiRunnerPtr);
        m_nativeThread.startUpdating();
    }

    @WorkerThread
    private void notifySurfaceDestroyed() {
        m_nativeThread.stopUpdating();
        m_surfaceHolder = null;
    }

    // begin implementation of SurfaceHolder.Callback
    @UiThread
    public void surfaceCreated(final SurfaceHolder holder) {
        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_surfaceHolder = holder;
                Surface surface = m_surfaceHolder.getSurface();
                if (surface.isValid()) {
                    nativeSetSurface(m_jniApiRunnerPtr, surface);
                    notifySurfaceCreated();

                }
            }
        });
    }

    @UiThread
    public void surfaceDestroyed(final SurfaceHolder holder) {
        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                if (m_surfaceHolder == holder) {
                    notifySurfaceDestroyed();
                }
            }
        });
    }

    @UiThread
    public void surfaceChanged(final SurfaceHolder holder, int format, int width, int height) {
        runOnNativeThread(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                m_surfaceHolder = holder;
                if (m_surfaceHolder != null) {
                    Surface surface = m_surfaceHolder.getSurface();
                    if (surface.isValid()) {
                        nativeSetSurface(m_jniApiRunnerPtr, surface);
                    }
                }
            }
        });
    }

    @WorkerThread
    private native long nativeCreateApiRunner(EegeoNativeMapView nativeMapView, Context context, AssetManager assetManager, float dpi, int density, String versionName, int versionCode);

    @WorkerThread
    private native void nativeDestroyApiRunner(long jniApiRunnerPtr);

    @WorkerThread
    private native void nativePauseApiRunner(long jniApiRunnerPtr);

    @WorkerThread
    private native void nativeResumeApiRunner(long jniApiRunnerPtr);

    @WorkerThread
    private native void nativeSetSurface(long jniApiRunnerPtr, Surface surface);

    @WorkerThread
    private native long nativeCreateEegeoMapApi(long jniApiRunnerPtr, EegeoMap eegeoMap, String apiKey, String coverageTreeManifest, String environmentThemesManifest);

    @WorkerThread
    private native void nativeUpdateApiRunner(long jniApiRunnerPtr, float deltaTimeSeconds);

    @WorkerThread
    private native void nativeProcessPointerDown(long jniApiRunnerPtr, int primaryActionIndex, int primaryActionIdentifier, int pointerCount, float[] x, float y[], int[] pointerIdentity, int[] pointerIndex);

    @WorkerThread
    private native void nativeProcessPointerUp(long jniApiRunnerPtr, int primaryActionIndex, int primaryActionIdentifier, int pointerCount, float[] x, float y[], int[] pointerIdentity, int[] pointerIndex);

    @WorkerThread
    private native void nativeProcessPointerMove(long jniApiRunnerPtr, int primaryActionIndex, int primaryActionIdentifier, int pointerCount, float[] x, float y[], int[] pointerIdentity, int[] pointerIndex);

    interface ICreateEegeoMapApi {
        @WorkerThread
        long create(EegeoMap eegeoMap, EegeoMapOptions eegeoMapOptions);
    }

    private class CreateEegeoMapApiImpl implements ICreateEegeoMapApi {
        @WorkerThread
        public long create(EegeoMap eegeoMap, EegeoMapOptions eegeoMapOptions) {
            String apiKey = EegeoApi.getInstance().getApiKey();
            String coverageTreeManifest = eegeoMapOptions.getCoverageTreeManifest();
            if (coverageTreeManifest == null) {
                coverageTreeManifest = "";
            }
            String environmentThemesManifest = eegeoMapOptions.getEnvironmentThemesManifest();
            if (environmentThemesManifest == null) {
                environmentThemesManifest = "";
            }
            long jniEegeoMapApiPtr = nativeCreateEegeoMapApi(m_jniApiRunnerPtr, eegeoMap, apiKey, coverageTreeManifest, environmentThemesManifest);
            return jniEegeoMapApiPtr;
        }
    }

    private class NativeThread extends Thread {
        private final float m_targetFramesPerSecond;
        private Handler m_nativeThreadHandler;
        private NativeUpdateApiRunner m_nativeUpdateApiRunner;
        private boolean m_updatingNative = false;
        private boolean m_threadExiting = false;

        @UiThread
        private NativeThread(float m_targetFramesPerSecond) {
            this.m_targetFramesPerSecond = m_targetFramesPerSecond;
        }

        @Override
        @UiThread
        public void start() {
            super.start();

            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException("failed to start NativeThread");
                }
            }

            if (m_nativeThreadHandler == null) {
                throw new RuntimeException("failed to create nativeThreadHandler");
            }
        }

        @UiThread
        public void postTo(Runnable runnable) {
            if (!m_threadExiting) {
                m_nativeThreadHandler.post(runnable);
            } else {
                Log.d("eegeo-android-sdk", "thread exiting, ignoring runnable");
            }
        }

        @WorkerThread
        public void startUpdating() {
            if (!m_updatingNative) {
                m_updatingNative = true;
                m_nativeThreadHandler.post(m_nativeUpdateApiRunner);
            }
        }

        @WorkerThread
        public void stopUpdating() {
            if (m_updatingNative) {
                m_updatingNative = false;
                m_nativeThreadHandler.removeCallbacks(m_nativeUpdateApiRunner);
            }
        }

        @UiThread
        public void quit() {
            //m_nativeThreadHandler.dump(new LogPrinter(Log.DEBUG, "eegeo-android-sdk"), "");
            m_threadExiting = true;
            m_nativeThreadHandler.removeCallbacksAndMessages(null);
            m_nativeThreadHandler.post(new Runnable() {
                @WorkerThread
                @Override
                public void run() {
                    Looper.myLooper().quit();
                }
            });
        }

        public void run() {
            Looper.prepare();

            m_nativeUpdateApiRunner = new NativeUpdateApiRunner();

            m_nativeThreadHandler = new Handler();

            synchronized (this) {
                notifyAll();
            }

            Looper.loop();
        }

        class NativeUpdateApiRunner implements Runnable {

            final private float m_frameThrottleDelaySeconds;
            private long m_startOfLastFrameNano;

            @WorkerThread
            NativeUpdateApiRunner() {
                this.m_startOfLastFrameNano = System.nanoTime();
                this.m_frameThrottleDelaySeconds = 1.f / m_targetFramesPerSecond;
            }

            @WorkerThread
            @Override
            public void run() {

                long timeNowNano = System.nanoTime();
                long nanoDelta = timeNowNano - m_startOfLastFrameNano;
                float deltaSeconds = (float) ((double) nanoDelta / 1e9);

                if (deltaSeconds > m_frameThrottleDelaySeconds) {
                    nativeUpdateApiRunner(m_jniApiRunnerPtr, deltaSeconds);
                    m_startOfLastFrameNano = timeNowNano;
                    m_nativeThreadHandler.post(this);
                } else {
                    long waitMS = Math.max(0, (long) (1000 * (m_frameThrottleDelaySeconds - deltaSeconds)));
                    m_nativeThreadHandler.postDelayed(this, waitMS);
                }

            }
        }
    }


}
