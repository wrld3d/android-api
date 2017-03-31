package com.eegeo.mapapi;

import android.support.annotation.UiThread;

public interface INativeMessageRunner {
    @UiThread
    void runOnNativeThread(Runnable runnable);
}
