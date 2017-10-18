package org.jocean.idiom;

import rx.functions.Action0;
import rx.functions.Action1;
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
    
    public static <E> DisposableWrapper<E> disposeOn(final TerminateAware<?> terminateAware,
            final DisposableWrapper<E> wrapper) {
        terminateAware.doOnTerminate(new Action0() {
            @Override
            public void call() {
                wrapper.dispose();
            }});
        return wrapper;
    }
    
    public static <E> Action1<DisposableWrapper<E>> disposeOn(final TerminateAware<?> terminateAware) {
        return new Action1<DisposableWrapper<E>>() {
            @Override
            public void call(final DisposableWrapper<E> wrapper) {
                disposeOn(terminateAware, wrapper);
            }};
    }
    
    public static void dispose(final Object obj) {
        if (obj instanceof DisposableWrapper) {
            ((DisposableWrapper<?>)obj).dispose();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Object unwrap(final Object obj) {
        if (obj instanceof DisposableWrapper) {
            return unwrap(((DisposableWrapper<Object>)obj).unwrap());
        } else {
            return obj;
        }
    }
}
