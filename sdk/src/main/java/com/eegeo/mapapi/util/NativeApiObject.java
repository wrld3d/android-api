package com.eegeo.mapapi.util;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.eegeo.mapapi.INativeMessageRunner;
import com.eegeo.mapapi.IUiMessageRunner;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;

public class NativeApiObject {

    protected final INativeMessageRunner m_nativeRunner;
    protected final IUiMessageRunner m_uiRunner;
    private final Queue<Runnable> m_tasks = new ArrayDeque<>(0);
    private Integer m_nativeHandle = null;

    @UiThread
    public NativeApiObject(INativeMessageRunner nativeRunner,
                           IUiMessageRunner uiRunner,
                           Callable<Integer> createHandleCallable) {
        this.m_nativeRunner = nativeRunner;
        this.m_uiRunner = uiRunner;

        new NativeHandleFuture(createHandleCallable)
                .then(new Ready<Integer>() {
                    @UiThread
                    @Override
                    public void ready(Integer nativeHandle) {
                        if (m_nativeHandle != null)
                            throw new RuntimeException();

                        if (nativeHandle == null)
                            throw new RuntimeException();

                        m_nativeHandle = nativeHandle;
                        while (!m_tasks.isEmpty()) {
                            m_nativeRunner.runOnNativeThread(m_tasks.poll());
                        }
                    }
                });
    }

    @WorkerThread
    protected boolean hasNativeHandle() {
        return m_nativeHandle != null;
    }

    @WorkerThread
    protected int getNativeHandle() {
        if (m_nativeHandle == null)
            throw new RuntimeException("nativeHandle not yet available, ensure all calls to getNativeHandle are wrapped by submit()");

        return m_nativeHandle.intValue();
    }

    @UiThread
    protected void destroyNativeHandle(Callable<Integer> destroyHandleCallable) {
        new NativeHandleFuture(destroyHandleCallable)
                .then(new Ready<Integer>() {
                    @Override
                    public void ready(Integer result) {
                        if (m_nativeHandle == null)
                            throw new RuntimeException();

                        if (!m_tasks.isEmpty())
                            throw new RuntimeException();

                        m_nativeHandle = null;
                    }
                });
    }

    @UiThread
    protected void submit(Runnable task) {
        if (m_nativeHandle == null) {
            m_tasks.add(task);
        } else {
            m_nativeRunner.runOnNativeThread(task);
        }
    }

    private class NativeHandleFuture extends Promise<Integer> {
        @UiThread
        public NativeHandleFuture(final Callable<Integer> createHandleCallable) {
            m_nativeRunner.runOnNativeThread(new Runnable() {

                @WorkerThread
                @Override
                public void run() {
                    try {
                        final Integer nativeHandle = createHandleCallable.call();
                        if (nativeHandle == null)
                            throw new RuntimeException("createHandleCallable failed to return non-null handle");

                        m_uiRunner.runOnUiThread(new Runnable() {
                            @UiThread
                            @Override
                            public void run() {
                                //Log.d("eegeo-android-sdk", String.format("native handle received: %1$d", nativeHandle));
                                NativeHandleFuture.this.ready(nativeHandle);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }
            });
        }
    }
}
