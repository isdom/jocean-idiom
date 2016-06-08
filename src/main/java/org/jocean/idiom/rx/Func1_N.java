package org.jocean.idiom.rx;

import rx.functions.Function;

/**
 * Represents a function with one-argument plus vector-argument.
 */
public interface Func1_N<T, R> extends Function {
    public R call(final T t, final Object... args);

    public static class Util {
        private static Func1_N<Object,Object> _RETURN_NULL = new Func1_N<Object,Object>() {
            @Override
            public Object call(final Object obj, final Object... args) {
                return null;
            }};
        @SuppressWarnings("unchecked")
        public static <T,R> Func1_N<T, R> returnNull() {
            return (Func1_N<T, R>)_RETURN_NULL;
        }
    }
}
