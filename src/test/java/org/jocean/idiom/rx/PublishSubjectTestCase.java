package org.jocean.idiom.rx;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class PublishSubjectTestCase {

    @Test
    public final void testPublishSubject() {
        final PublishSubject<Integer> subject = PublishSubject.create();

        subject.onNext(3);

        final TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        Observable.just(1,2).concatWith(subject).subscribe( subscriber);

        subject.onNext(4);
        subject.onCompleted();

        subscriber.assertReceivedOnNext(Arrays.asList(1,2,4));
        subscriber.assertCompleted();
    }

}
