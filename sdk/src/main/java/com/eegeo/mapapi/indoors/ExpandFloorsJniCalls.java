package com.eegeo.mapapi.indoors;

import android.support.annotation.WorkerThread;

public class ExpandFloorsJniCalls {
    @WorkerThread
    public static native void expandIndoor(long jniEegeoMapApiPtr);

    @WorkerThread
    public static native void collapseIndoor(long jniEegeoMapApiPtr);
}
