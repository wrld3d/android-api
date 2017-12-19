package com.wrld.widgets.ui;

import android.view.animation.Animation;

public class UiScreenVisibilityState implements UiScreenMemento<UiScreenVisibilityState>{

    private UiScreenController m_screenController;
    private UiScreenController.ScreenState m_stateAtTime;

    public UiScreenVisibilityState getState() {return this;}

    public UiScreenVisibilityState(UiScreenController screenController){
        m_screenController = screenController;
        m_stateAtTime = screenController.getScreenState();
    }

    public void apply(){
        if(m_screenController.getScreenState() != m_stateAtTime){
            resetState();
        }
    }

    private void resetState(){
        Animation resetAnim;
        if(m_stateAtTime == UiScreenController.ScreenState.GONE){
            resetAnim = m_screenController.transitionToGone();
        }
        else{
            resetAnim = m_screenController.transitionToVisible();
        }
        resetAnim.start();
    }
}
