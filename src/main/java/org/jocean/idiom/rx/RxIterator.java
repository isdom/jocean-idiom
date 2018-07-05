package org.jocean.idiom.rx;

import rx.Observable;
import rx.Single;

public interface RxIterator<E> {
    public Single<Boolean> hasNext();

    public Observable<? extends RxIterator<E>> next();

    public Observable<? extends E> payload();
}
