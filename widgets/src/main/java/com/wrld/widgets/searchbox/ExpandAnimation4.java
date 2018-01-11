package com.wrld.widgets.searchbox;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout.LayoutParams;

class ExpandAnimation4 extends Animation {

    private final int m_startHeight;
    private final int m_deltaHeight;

    private View m_view;

    private LayoutParams lp;

    public ExpandAnimation4(View view, int start, int end) {
        m_view = view;
        m_startHeight = start;
        m_deltaHeight = end - start;
        lp = (LayoutParams) m_view.getLayoutParams();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        lp.height = (int)(m_startHeight + (m_deltaHeight * interpolatedTime));
        //m_view.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}