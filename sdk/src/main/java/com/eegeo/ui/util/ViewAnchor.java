// Copyright eeGeo Ltd (2012-2014), All Rights Reserved

package com.eegeo.ui.util;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import android.view.View;
import android.graphics.Point;
import android.graphics.PointF;

public class ViewAnchor
{
    /**
     * Anchors a View to a point relative to its parent. This will anchor a point on the View to a
     * point on its parent. The anchorUV is relative to the size of the View, a value of (0,0) will
     * anchor the top left corner of the view and a value of (1,1) will anchor the bottom right
     * corner of the view. Negative and values larger than 1 are also valid for anchorUV.
     *
     * @param view The View to set the position of.
     * @param screenPoint The point to anchor to, relative to the parent of the View.
     * @param anchorUV The point on the View to use as the anchor point.
     */
    @UiThread
    public static void positionView(@NonNull View view, @NonNull Point screenPoint, @NonNull PointF anchorUV)
    {
        view.setX(screenPoint.x - (view.getWidth()  * anchorUV.x));
        view.setY(screenPoint.y - (view.getHeight() * anchorUV.y));
    }
}
