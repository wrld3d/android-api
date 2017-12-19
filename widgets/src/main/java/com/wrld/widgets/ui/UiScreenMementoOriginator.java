package com.wrld.widgets.ui;

public interface UiScreenMementoOriginator<T> {
    UiScreenMemento<T> generateMemento();
    void resetTo(UiScreenMemento<T> memento);
}
