package org.jocean.idiom;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

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
    
    public static <E> DisposableWrapper<E> wrap(final E element, final Action1<E> disposer) {
        final Subscription subscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                if (null != disposer) {
                    disposer.call(element);
                }
            }
        });
        return new DisposableWrapper<E>() {

            @Override
            public int hashCode() {
                return unwrap().hashCode();
            }

            @Override
            public boolean equals(final Object o) {
                return unwrap().equals(DisposableWrapperUtil.unwrap(o));
            }

            @Override
            public E unwrap() {
                return element;
            }

            @Override
            public void dispose() {
                subscription.unsubscribe();
            }

            @Override
            public boolean isDisposed() {
                return subscription.isUnsubscribed();
            }

            @Override
            public String toString() {
                return "DisposableWrapper[" + unwrap().toString() + "]";
            }
        };
    }

    public static <E> Func1<E, DisposableWrapper<E>> wrap(final Action1<E> disposer,
            final TerminateAware<?> terminateAware) {
        return new Func1<E, DisposableWrapper<E>>() {
            @Override
            public DisposableWrapper<E> call(final E element) {
                return disposeOn(terminateAware, wrap(element, disposer));
            }
        };
    }
    
    public static <E> DisposableWrapper<E> disposeOn(final TerminateAware<?> terminateAware,
            final DisposableWrapper<E> wrapper) {
        if (null!=terminateAware) {
            terminateAware.doOnTerminate(new Action0() {
                @Override
                public void call() {
                    wrapper.dispose();
                }});
        }
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
