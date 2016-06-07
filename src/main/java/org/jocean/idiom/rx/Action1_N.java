package org.jocean.idiom.rx;

import rx.functions.Action;

/**
 * A one-argument plus vector-argument action.
 */
public interface Action1_N<T> extends Action {
    void call(final T t, final Object... args);
    
    static class Util {
        private static final Action1_N<Object> EMPTY = new Action1_N<Object>() {
            @Override
            public void call(Object t, Object... args) {
                //  do nothing
            }
        };
            
        @SuppressWarnings("unchecked")
        public static final <T> Action1_N<T> empty() {
            return (Action1_N<T>)EMPTY;
        }
    }
}
