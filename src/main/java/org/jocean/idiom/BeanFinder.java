package org.jocean.idiom;

import rx.Observable;

public interface BeanFinder {
    public <T> Observable<T> find(final Class<T> requiredType);
}
