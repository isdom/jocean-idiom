package org.jocean.idiom;

import rx.Observable;

public interface BeanFinder {

    public <T> Observable<T> find(final Class<T> requiredType);

    public <T> Observable<T> find(final String name, final Class<T> requiredType);

    public Observable<Object> find(final String name, final Object... args);

    public <T> Observable<T> find(final Class<T> requiredType, final Object... args);
}
