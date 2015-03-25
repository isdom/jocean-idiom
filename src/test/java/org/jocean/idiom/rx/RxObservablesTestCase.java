package org.jocean.idiom.rx;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

public class RxObservablesTestCase {

    @Test
    public void testIgnoreCompleted() {
        final TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
        
        Observable.just("hello", "world")
            .compose(RxObservables.<String> ignoreCompleted())
            .subscribe(testSubscriber);
        
        testSubscriber.assertReceivedOnNext(Arrays.asList("hello", "world"));
        testSubscriber.assertNoErrors();
        // onCompleted not called
        assertTrue(testSubscriber.getOnCompletedEvents().isEmpty());
    }

    @Test
    public void testFlatMapOnCompleted() {
        final TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
        
        Observable.just("hello", "world")
            .flatMap(new Func1<String, Observable<String>>() {
                @Override
                public Observable<String> call(String src) {
                    return Observable.just(src, src+"?");
                }})
            .subscribe(testSubscriber);
        
        testSubscriber.awaitTerminalEvent();
        
        testSubscriber.assertReceivedOnNext(Arrays.asList("hello", "hello?", "world", "world?"));
        // no error
        testSubscriber.assertNoErrors();
        // onCompleted called
        assertEquals(1, testSubscriber.getOnCompletedEvents().size());
    }
}
