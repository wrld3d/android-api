package com.eegeo.mapapi.precaching;

import androidx.annotation.WorkerThread;

class PrecacheApiJniCalls {
    @WorkerThread
    static native int beginPrecacheOperation(
            long jniEegeoMapApiPtr, double latitudeDegrees, double longitudeDegrees, double radius);

    @WorkerThread
    static native void cancelPrecacheOperation(long jniEegeoMapApiPtr, int operationId);

    @WorkerThread
    static native double getMaximumPrecacheRadius();
}
