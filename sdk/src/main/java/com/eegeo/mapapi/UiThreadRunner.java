package com.eegeo.mapapi;

import android.content.Context;
import android.os.Handler;

public class UiThreadRunner implements IUiMessageRunner {
    private final Handler m_handler;

    public UiThreadRunner(Context context) {
        m_handler = new Handler(context.getMainLooper());
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        m_handler.post(runnable);
    }
}
