package org.jocean.idiom.rx;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
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

}
