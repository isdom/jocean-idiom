package org.jocean.idiom;

import rx.functions.Action0;

public interface DisposableWrapper<E> {

    public E unwrap();

    public void dispose();

    public boolean isDisposed();

    public void doOnDisposed(final Action0 action);
}
