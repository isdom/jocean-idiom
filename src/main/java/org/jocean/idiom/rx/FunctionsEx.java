package org.jocean.idiom.rx;

import rx.functions.FuncN;

public class FunctionsEx {
    private FunctionsEx() {
        throw new IllegalStateException("No instances!");
    }

    public static <R> FuncN<R> fromConstant(final R r) {
        return new FuncN<R>() {
            @Override
            public R call(final Object... args) {
                return r;
            }};
    }
}
