package org.jocean.idiom.rx;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

public interface DoOnUnsubscribe extends Action1<Subscription> {
    public static class Util {
        public final static DoOnUnsubscribe UNSUBSCRIBE_NOW = new DoOnUnsubscribe() {
            @Override
            public void call(final Subscription s) {
                if (!s.isUnsubscribed()) {
                    s.unsubscribe();
                }
            }};
            
        public static DoOnUnsubscribe from(final Subscriber<?> subscriber) {
            return new DoOnUnsubscribe() {
                @Override
                public void call(final Subscription s) {
                    subscriber.add(s);
                }};
        }
    }
}

