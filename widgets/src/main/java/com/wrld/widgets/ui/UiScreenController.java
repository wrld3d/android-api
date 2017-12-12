package com.wrld.widgets.ui;

import android.view.animation.Animation;

public interface UiScreenController {
    enum ScreenState {VISIBLE, GONE };

    Animation transitionToVisible();
    Animation transitionToGone();
    // TODO Animation cancelCurrentTransition();

    ScreenState getScreenState();
}
