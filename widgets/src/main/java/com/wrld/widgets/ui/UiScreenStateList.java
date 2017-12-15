package com.wrld.widgets.ui;

import android.view.animation.Animation;

import java.util.ArrayList;

public class UiScreenStateList {

    private class UiScreenState{
        private UiScreenController m_screenController;
        private UiScreenController.ScreenState m_stateAtTime;

        public UiScreenState(UiScreenController screen){
            m_screenController = screen;
            m_stateAtTime = screen.getScreenState();
        }

        public void setToStateAtTime(){
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

    private ArrayList<UiScreenState> m_statesAtTime;

    public UiScreenStateList(ArrayList<UiScreenController> screens){
        m_statesAtTime = new ArrayList<UiScreenState>();
        for(UiScreenController screen : screens){
            m_statesAtTime.add(new UiScreenState(screen));
        }
    }

    public void resetTo(){
        for(UiScreenState screenState : m_statesAtTime){
            screenState.setToStateAtTime();
        }
    }
}
