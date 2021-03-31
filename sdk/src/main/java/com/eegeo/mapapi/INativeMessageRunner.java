package com.eegeo.mapapi;

import androidx.annotation.UiThread;

public interface INativeMessageRunner {
    @UiThread
    void runOnNativeThread(Runnable runnable);
}
