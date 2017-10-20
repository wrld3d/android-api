// Copyright eeGeo Ltd (2012-2014), All Rights Reserved

package com.eegeo.ui.util;

import android.view.View;
import android.graphics.Point;
import android.graphics.PointF;

public class ViewAnchor
{
    public static void positionView(View view, Point screenPoint, PointF anchorUV)
    {
        view.setX(screenPoint.x - (view.getWidth()  * anchorUV.x));
        view.setY(screenPoint.y - (view.getHeight() * anchorUV.y));
    }
}
