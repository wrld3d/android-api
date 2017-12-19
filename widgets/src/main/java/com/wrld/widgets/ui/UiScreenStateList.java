package com.wrld.widgets.ui;

import java.util.ArrayList;

public class UiScreenStateList {

    private class UiScreenState<T>{
        private UiScreenMementoOriginator<T> m_originator;
        private UiScreenMemento<T> m_memento;

        public UiScreenState(UiScreenMementoOriginator<T> originator){
            m_originator = originator;
            m_memento = originator.generateMemento();
        }

        public void setToStateAtTime(){
            m_originator.resetTo(m_memento);
        }
    }

    private ArrayList<UiScreenState> m_statesAtTime;

    public UiScreenStateList(ArrayList<UiScreenMementoOriginator> screens){
        m_statesAtTime = new ArrayList<UiScreenState>();
        for(UiScreenMementoOriginator originator : screens){
            m_statesAtTime.add(new UiScreenState(originator));
        }
    }

    public void resetTo(){
        for(UiScreenState screenState : m_statesAtTime){
            screenState.setToStateAtTime();
        }
    }
}
