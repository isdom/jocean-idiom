package org.jocean.idiom.rx;

import rx.Subscriber;

public class RxSubscribers {
    private RxSubscribers() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> Subscriber<? super T> guardUnsubscribed(final Subscriber<? super T> subscriber) {
        if (subscriber instanceof GuardUnsubscribedSubscriber) {
            return subscriber;
        }
        else {
            return new GuardUnsubscribedSubscriber<T>(subscriber);
        }
    }
}
