package org.jocean.idiom.rx;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.jocean.idiom.ExceptionUtils;
import org.jocean.idiom.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.FuncN;

public class RxFunctions {
    private static final Logger LOG =
            LoggerFactory.getLogger(RxFunctions.class);

    private RxFunctions() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> Transformer<T, T> transformBy(final Observable<? extends Transformer<T, T>> provider) {
        return new Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> source) {
                return provider.flatMap(new Func1<Transformer<T, T>, Observable<T>>() {
                            @Override
                            public Observable<T> call(final Transformer<T, T> trans) {
                                return source.compose(trans);
                            }
                        }).onErrorResumeNext(source);
            }
        };
    }

    public static <R> FuncN<R> fromConstant(final R r) {
        return new FuncN<R>() {
            @Override
            public R call(final Object... args) {
                return r;
            }};
    }

    public static <T> Transformer<T, T> countDownOnUnsubscribe(final CountDownLatch latch) {
        return new Transformer<T,T>() {
            @Override
            public Observable<T> call(final Observable<T> source) {
                return source.doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        latch.countDown();
                    }});
            }};
    }

    public static <R> Func0<R> toFunc0(final FuncN<R> func) {
        return new Func0<R>() {
            @Override
            public R call() {
                return func.call();
            }};
    }

    public static <T, R> Func1<T, R> toFunc1(final FuncN<R> func) {
        return new Func1<T, R>() {
            @Override
            public R call(final T t) {
                return func.call(t);
            }};
    }

    public static <T0, T1, R> Func2<T0, T1, R> toFunc2(final FuncN<R> func) {
        return new Func2<T0, T1, R>() {
            @Override
            public R call(final T0 t1, final T1 t2) {
                return func.call(t1, t2);
            }};
    }

    public static <T0, T1, T2, R> Func3<T0, T1, T2, R> toFunc3(final FuncN<R> func) {
        return new Func3<T0, T1, T2, R>() {
            @Override
            public R call(final T0 t0, final T1 t1, final T2 t2) {
                return func.call(t0, t1, t2);
            }};
    }

    public static <T, R> Func1_N<T, R> toFunc1_N(final Class<T> cls, final String methodName) {
        final Method method = ReflectUtils.getMethodNamed(cls, methodName);
        if (null!=method) {
            return new Func1_N<T, R>() {
                @SuppressWarnings("unchecked")
                @Override
                public R call(final T obj, final Object... args) {
                    try {
                        return (R)method.invoke(obj, args);
                    } catch (final Exception e) {
                        LOG.warn("exception when invoke {}.{}, detail: {}",
                                cls, method, ExceptionUtils.exception2detail(e));
                        return null;
                    }
                }};
        } else {
            throw new RuntimeException("invalid method " + methodName +" for class " + cls);
        }
    }
}
