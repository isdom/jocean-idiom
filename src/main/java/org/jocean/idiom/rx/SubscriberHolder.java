package org.jocean.idiom.rx;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.Subscriber;

public class SubscriberHolder<T> implements Observable.OnSubscribe<T> {

    @Override
    public void call(final Subscriber<? super T> subscriber) {
        this._subscribers.add(subscriber);
    }
    
    public Subscriber<? super T> getAt(final int idx) {
        return this._subscribers.get(idx);
    }
    
    public int getSubscriberCount() {
        return this._subscribers.size();
    }

    private final List<Subscriber<? super T>> _subscribers = new CopyOnWriteArrayList<>();
}
