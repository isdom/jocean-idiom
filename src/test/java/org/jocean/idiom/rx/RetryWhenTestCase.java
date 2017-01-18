package org.jocean.idiom.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.jocean.idiom.rx.RxObservables.RetryPolicy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;

public class RetryWhenTestCase {
    
    private static final Logger LOG =
            LoggerFactory.getLogger(RetryWhenTestCase.class);
    
    public static class TestException extends RuntimeException {
        private static final long serialVersionUID = 1503241798271313102L;
    };

    private static Observable<?> buildAlwaysFailWith(final Throwable throwable) {
        return Observable.create(new OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                LOG.info("onSubscribe for {}", subscriber);
                subscriber.onError(throwable);
            }});
    }
    
    @Test(expected = RuntimeException.class)
    public final void testRetryIfMatchForNotMatch() {
        final AtomicInteger counter = new AtomicInteger(0);
        
        try {
            final Observable<?> alwaysFail = buildAlwaysFailWith(new RuntimeException());
            alwaysFail
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable t) {
                    counter.incrementAndGet();
                }})
            .retryWhen(RxObservables.retryWith(new RetryPolicy<Object>() {
                @Override
                public Observable<Object> call(Observable<Throwable> errors) {
                    return errors.compose(RxObservables.retryIfMatch(TestException.class));
                }}))
            .toBlocking().first();
        } finally {
            assertEquals(1, counter.get());
        }
    }

    @Test
    public final void testRetryIfMatchForMatchAndRetryMaxTimes() {
        final AtomicInteger counter = new AtomicInteger(0);
        
        @SuppressWarnings("unchecked")
        final Observable<Integer> alwaysFail = (Observable<Integer>) buildAlwaysFailWith(new TestException());
        alwaysFail
        .doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                counter.incrementAndGet();
            }})
        .retryWhen(RxObservables.retryWith(new RetryPolicy<Integer>() {
            @Override
            public Observable<Integer> call(Observable<Throwable> errors) {
                return errors.compose(RxObservables.retryIfMatch(TestException.class))
                        .compose(RxObservables.retryMaxTimes(1))
                        ;
            }}))
        .toBlocking().singleOrDefault(0);
        
        assertEquals(2, counter.get());
    }

    @Test(timeout=2000)
    public final void testRetryIfMatchForRetryMaxTimesAndDelayTo() {
        final AtomicInteger counter = new AtomicInteger(0);
        final long startms = System.currentTimeMillis();
        
        @SuppressWarnings("unchecked")
        final Observable<Integer> alwaysFail = (Observable<Integer>) buildAlwaysFailWith(new TestException());
        alwaysFail
        .doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable t) {
                counter.incrementAndGet();
            }})
        .retryWhen(RxObservables.retryWith(new RetryPolicy<Integer>() {
            @Override
            public Observable<Integer> call(Observable<Throwable> errors) {
                return errors.compose(RxObservables.retryMaxTimes(1))
                        .compose(RxObservables.retryDelayTo(1))
                        ;
            }}))
        .toBlocking().singleOrDefault(0);
        
        final long duration = System.currentTimeMillis() - startms;
        LOG.info("duration in ms is {} ms", duration);
        assertTrue(duration >= 1000);
        assertEquals(2, counter.get());
    }
}
