package com.eegeo.mapapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Callbacks {
    public interface ICallback0 {
        void onCallback();
    }

    public interface ICallback1<T> {
        void onCallback(T t);
    }

    public interface ICallback2<T1, T2> {
        void onCallback(T1 t1, T2 t2);
    }

    public interface ICallback3<T1, T2, T3> {
        void onCallback(T1 t1, T2 t2, T3 t3);
    }

    public static class CallbackCollection<T> {
        protected List<T> m_callbacks = new ArrayList<T>();

        public void add(T t) {
            m_callbacks.add(t);
        }

        public void remove(T t) {
            m_callbacks.remove(t);
        }
    }

    public static final class CallbackCollection0 extends CallbackCollection<ICallback0> implements ICallback0 {
        @Override
        public void onCallback() {
            if (m_callbacks.size() > 0) {
                ListIterator<ICallback0> it = m_callbacks.listIterator();
                while (it.hasNext()) {
                    it.next().onCallback();
                }
            }
        }
    }

    public static final class CallbackCollection1<T> extends CallbackCollection<ICallback1<T>> implements ICallback1<T> {
        @Override
        public void onCallback(T t) {
            if (m_callbacks.size() > 0) {
                ListIterator<ICallback1<T>> it = m_callbacks.listIterator();
                while (it.hasNext()) {
                    it.next().onCallback(t);
                }
            }
        }
    }

    public static final class CallbackCollection2<T1, T2> extends CallbackCollection<ICallback2<T1, T2>> implements ICallback2<T1, T2> {
        @Override
        public void onCallback(T1 t1, T2 t2) {
            if (m_callbacks.size() > 0) {
                ListIterator<ICallback2<T1, T2>> it = m_callbacks.listIterator();
                while (it.hasNext()) {
                    it.next().onCallback(t1, t2);
                }
            }
        }
    }

    public static final class CallbackCollection3<T1, T2, T3> extends CallbackCollection<ICallback3<T1, T2, T3>> implements ICallback3<T1, T2, T3> {
        @Override
        public void onCallback(T1 t1, T2 t2, T3 t3) {
            if (m_callbacks.size() > 0) {
                ListIterator<ICallback3<T1, T2, T3>> it = m_callbacks.listIterator();
                while (it.hasNext()) {
                    it.next().onCallback(t1, t2, t3);
                }
            }
        }
    }
}