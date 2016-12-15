package org.jocean.idiom.rx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
import rx.functions.Func1;
import rx.functions.Func2;
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

    public static Transformer<? super Throwable, ? extends Object> retryIfMatch(
            final Class<? extends Throwable> clazzException) {
        return retryIfMatch(clazzException, 100);
    }
    
    public static Transformer<? super Throwable, ? extends Object> retryIfMatch(
            final Class<? extends Throwable> clazzException, final long delayOfCompletedInMs) {
        final Func1<Throwable, Observable<?>> func1 = new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(final Throwable error) {
                // For TransportException, we retry
                if (clazzException.isInstance(error)) {
                    LOG.info("retryIfMatch: match error({}), start to retry with delay completed {} ms", 
                            ExceptionUtils.exception2detail(error), delayOfCompletedInMs);
                    return Observable.concat(Observable.just(null), 
                        // 此处通过 concat + empty + delay 添加延时的 completed 触发, 原因是：
                        /**
                         * reference: http://blog.danlew.net/2016/01/25/rxjavas-repeatwhen-and-retrywhen-explained/
                         * --------------------------------------------------------------------------------------------
                         * The emissions of the Observable<?> returned determines whether or not resubscription happens. 
                         * If it emits onCompleted or onError then it doesn't resubscribe. But if it emits onNext then 
                         * it does (regardless of what's actually in the onNext). That's why it's using the wildcard for 
                         * its generic type: it's just the notification (next, error or completed) that matters.
                         *  翻译:
                         *  被返回的 Observable<?> 所要发送的事件决定了重订阅是否会发生。如果发送的是 onCompleted 或者 
                         *  onError 事件，将不会触发重订阅。相对的，如果它发送 onNext 事件，则触发重订阅（不管onNext 实际上是什么事件）。
                         *  这就是为什么使用了通配符作为泛型类型：这仅仅是个通知（next, error或者completed），一个很重要的通知而已。
                         * --------------------------------------------------------------------------------------------
                         *  实验发现，如果直接返回 just(null)，由于在 onNext(null) 之后 立刻会调用 onCompleted()
                         *  因此 re-subscribe 的行为没有被触发，推测，紧接着的 onCompleted() 阻止了re-subscribe，
                         *  故而需要延后 onCompleted() 调用时机，即 concat + empty + delay 添加延时的 completed 触发
                         */
                        Observable.empty().delay(delayOfCompletedInMs, TimeUnit.MILLISECONDS));
                }

                LOG.info("retryIfMatch: NOT match {} bcs of error({}), abort retry", 
                        clazzException, ExceptionUtils.exception2detail(error));
                // For anything else, don't retry
                return Observable.error(error);
            }
        };
        
        return new Transformer<Throwable, Object>() {
            @Override
            public Observable<Object> call(final Observable<Throwable> source) {
                return source.flatMap(func1);
            }};
    }
    
    public static Transformer<? super Object, ? extends Integer> retryMaxTimes(final int maxTimes) {
        return retryMaxTimes(maxTimes, 100);
    }
    
    public static Transformer<? super Object, ? extends Integer> retryMaxTimes(
            final int maxTimes, final long delayOfCompletedInMs) {
        return new Transformer<Object, Integer>() {
            @Override
            public Observable<Integer> call(final Observable<Object> source) {
                return source.zipWith(Observable.<Integer>concat(Observable.range(1, maxTimes), 
                        // 此处通过 concat + empty + delay 添加延时的 completed 触发, 原因参见 retryIfMatch 中注释
                        Observable.<Integer>empty().delay(delayOfCompletedInMs, TimeUnit.MILLISECONDS)),
                        new Func2<Object, Integer, Integer>() {
                            @Override
                            public Integer call(final Object obj, final Integer times) {
                                LOG.info("retryMaxTimes: retry for NO.({}) with delay completed {} ms",
                                        times, delayOfCompletedInMs); 
                                return times;
                            }
                        });
            }
        };
    }

    public static Transformer<? super Integer, ? extends Object> retryDelayTo(
            final int delayBaseInSecond) {
        final Func1<Integer, Observable<? extends Object>> func1 = 
        new Func1<Integer, Observable<? extends Object>>() {
            @Override
            public Observable<? extends Object> call(final Integer retryCount) {
                final long interval = (long) Math.pow(delayBaseInSecond, retryCount);
                LOG.info("retryDelayTo: retry for NO.({}) and delay for {} second(s)",
                        retryCount, interval); 
                return Observable.timer(interval, TimeUnit.SECONDS);
            }
        };
        return new Transformer<Integer, Object>() {
            @Override
            public Observable<Object> call(final Observable<Integer> source) {
                return source.flatMap(func1);
            }};
    }

    public interface RetryPolicy<T> extends Transformer<Throwable,T> {};

    public static <T> Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> retryWith(
            final RetryPolicy<T> policy) {
        return new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Observable<?> call(final Observable<? extends Throwable> errors) {
                return policy.call((Observable<Throwable>)errors);
            }
        };
    }
    
    private static final Transformer<Object, Object> ENSURE_SUBSCR_ATMOSTONCE = 
        new Transformer<Object, Object>() {
        @Override
        public Observable<Object> call(final Observable<Object> source) {
            final AtomicBoolean hasSubscribed = new AtomicBoolean(false);
            
            return source.doOnSubscribe(new Action0() {
                @Override
                public void call() {
                    if (hasSubscribed.compareAndSet(false, true)) {
                        LOG.info("Observable ({}) is subscribed now", source);
                    } else {
                        LOG.warn("Observable ({}) is subscribed already, can't subscribe again!", 
                                source);
                        // operator has subscribed before, throw Exception
                        throw new RuntimeException(source + " can't subscribed more than once");
                    }
                }})
            ;
        }
    };
    
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T,T> ensureSubscribeAtmostOnce() {
        return (Transformer<T,T>)ENSURE_SUBSCR_ATMOSTONCE;
    }
    
}
