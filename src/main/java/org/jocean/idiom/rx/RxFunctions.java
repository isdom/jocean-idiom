package org.jocean.idiom.rx;

import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Action0;
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
}
