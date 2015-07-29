package org.jocean.idiom.rx;

import java.util.Map;

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
}
