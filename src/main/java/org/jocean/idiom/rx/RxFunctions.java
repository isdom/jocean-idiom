package org.jocean.idiom.rx;

import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.ActionN;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;

public class RxFunctions {
    private RxFunctions() {
        throw new IllegalStateException("No instances!");
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
}
