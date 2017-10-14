package org.jocean.idiom;

import rx.functions.Func1;

public class DisposableWrapperUtil {
    private DisposableWrapperUtil() {
        throw new IllegalStateException("No instances!");
    }
    
    public static <E> Func1<DisposableWrapper<E>, E> unwrap() {
        return new Func1<DisposableWrapper<E>, E>() {
            @Override
            public E call(final DisposableWrapper<E> wrapper) {
                return wrapper.unwrap();
            }
        };
    }
}
