package com.wrld.widgets.searchbox;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

class ExpandAnimation extends Animation {

    private final float mStartWeight;
    private final float mDeltaWeight;

    private View m_view;

    public ExpandAnimation(View view, float startWeight, float endWeight) {
        m_view = view;
        mStartWeight = startWeight;
        mDeltaWeight = endWeight - startWeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_view.getLayoutParams();
        lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
        m_view.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}