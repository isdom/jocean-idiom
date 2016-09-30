package org.jocean.idiom.rx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jocean.idiom.ExceptionUtils;
import org.jocean.idiom.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.subscriptions.Subscriptions;

public class RxObservables {
    private static final Logger LOG =
            LoggerFactory.getLogger(RxObservables.class);

    private RxObservables() {
        throw new IllegalStateException("No instances!");
    }

    protected static enum OperatorIgnoreCompleted implements Operator<Object, Object> {
        IGNORE_COMPLETED;

        @Override
        public Subscriber<Object> call(final Subscriber<Object> child) {
            return new Subscriber<Object>(child) {
                @Override
                public void onCompleted() {
                    // Do Nothing
                }
                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }
                @Override
                public void onNext(Object t) {
                    child.onNext(t);
                }
            };
        }
    }
    
    private static final Transformer<Object, Object> IGNORE_COMPLETED_TRANSFORMER = 
        new Transformer<Object, Object>() {
            @Override
            public Observable<Object> call(final Observable<Object> actual) {
                return actual.lift(OperatorIgnoreCompleted.IGNORE_COMPLETED);
            }};
    
    @SuppressWarnings("unchecked")
    public static final <T> Transformer<T, T> ignoreCompleted() {
        return (Transformer<T, T>)IGNORE_COMPLETED_TRANSFORMER;
    }
    
    public static final <T, U> Observable<T> delaySubscriptionUntilCompleted(
            final Observable<T> source, final Observable<U> selector) {
        return source.delaySubscription(new Func0<Observable<U>>() {
            @Override
            public Observable<U> call() {
                return selector.last();
            }});
    }

    //  for Action0 addListener(final T listener)
    public static <T> Action1<? super Action1<T>> asOnNext(final T listener) {
        return new  Action1<Action1<T>>() {
            @Override
            public void call(final Action1<T> action) {
                action.call(listener);
            }};
    }
    
    //  for Action0 addListener(final T listener)
    public static <T> Observable<Action1<T>> fromAddListener(
            final Object target, final String addListenerMethod, final Class<T> listenerCls) {
        final Method method = ReflectUtils.getMethodOf(target.getClass(), addListenerMethod, listenerCls);
        if (null != method) {
            return Observable.create(new OnSubscribe<Action1<T>>() {
                @Override
                public void call(final Subscriber<? super Action1<T>> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        try {
                            final Action0 unsubscribe = (Action0)method.invoke(
                                    target, buildListener(listenerCls, subscriber));
                            if (null != unsubscribe) {
                                subscriber.add(Subscriptions.create(unsubscribe));
                            }
                        } catch (Exception e) {
                            LOG.warn("exception when invoke {}/{}, detail: {}",
                                    target, method, ExceptionUtils.exception2detail(e));
                        }
                    }
                }});
        } else {
            LOG.warn("can't found method named[{}({})] for class({}), failed to fromAddListener",
                    addListenerMethod, listenerCls, target.getClass());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T buildListener(final Class<T> listenerCls,
            final Subscriber<? super Action1<T>> subscriber) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return (T) Proxy.newProxyInstance(cl, new Class<?>[]{listenerCls}, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {
                // An invocation of the hashCode, equals, or toString methods
                // declared in java.lang.Object on a proxy instance will be 
                // encoded and dispatched to the invocation handler's invoke
                // method in the same manner as interface method invocations are
                // encoded and dispatched, as described above. The declaring 
                // class of the Method object passed to invoke will be
                // java.lang.Object. Other public methods of a proxy instance
                // inherited from java.lang.Object are not overridden by a proxy
                // class, so invocations of those methods behave like they do
                // for instances of java.lang.Object.
                if (method.getName().equals("hashCode")) {
                    return this.hashCode();
                } else if (method.getName().equals("equals")) {
                    return (this == args[0]);
                } else if (method.getName().equals("toString")) {
                    return this.toString();
                }
                final Action1<T> action = new Action1<T>() {
                    @Override
                    public void call(final T t) {
                        try {
                            method.invoke(t, args);
                        } catch (Exception e) {
                            LOG.warn("exception when invoke {}/{} for args {}, detail: {}",
                                    t, method, args, ExceptionUtils.exception2detail(e));
                        }
                    }};
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(action);
                }
                return null;
            }});
    }
}
