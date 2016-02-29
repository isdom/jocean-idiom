package org.jocean.idiom.rx;

import java.util.Collection;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action0;

public class RxActions {
    private RxActions() {
        throw new IllegalStateException("No instances!");
    }

    public static <K, V> Action0 doPut(final Map<K,V> map, final K key, final V value) {
        map.put(key, value);
        return new Action0() {
            @Override
            public void call() {
                map.remove(key);
            }};
    }

    public static <V> Action0 doAdd(final Collection<V> collection, final V value) {
        collection.add(value);
        return new Action0() {
            @Override
            public void call() {
                collection.remove(value);
            }};
    }
    
    public static Action0 subscription2Action0(final Subscription subscription) {
        return new Action0() {
            @Override
            public void call() {
                if (!subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }};
    }
}
