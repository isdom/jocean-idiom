package org.jocean.idiom.rx;

import rx.functions.Function;

/**
 * Represents a function with one-argument plus vector-argument.
 */
public interface Func1_N<T, R> extends Function {
    R call(final T t, final Object... args);
}
