package org.jocean.idiom.rx;

import rx.functions.Action;

/**
 * A one-argument plus vector-argument action.
 */
public interface Action1_N<T> extends Action {
    void call(final T t, final Object... args);
}
