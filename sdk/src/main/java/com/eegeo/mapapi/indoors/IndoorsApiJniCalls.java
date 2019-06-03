package com.eegeo.mapapi.indoors;

import android.support.annotation.WorkerThread;

public class IndoorsApiJniCalls {
    @WorkerThread
    public static native IndoorMap getIndoorMapData(long jniEegeoMapApiPtr);

    @WorkerThread
    public static native int getCurrentFloorIndex(long jniEegeoMapApiPtr);

    @WorkerThread
    public static native void exitIndoorMap(long jniEegeoMapApiPtr);

    @WorkerThread
    public static native void floorSelected(long jniEegeoMapApiPtr, int selectedFloor);

    @WorkerThread
    public static native void floorSelectionDragged(long jniEegeoMapApiPtr, float dragParam);

    @WorkerThread
    public static native void enterIndoorMap(long jniEegeoMapApiPtr, String mapId);

    @WorkerThread
    public static native void setExitIndoorWhenTooFarAway(long jniEegeoMapApiPtr, boolean exitWhenFarAway);
}
