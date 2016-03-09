package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicReference;

import rx.functions.Action1;

public class DestroyableRef<T> {

    public DestroyableRef(final T impl) {
        this._ref = new AtomicReference<T>(impl);
    }
    
    public void destroy(final Action1<T> action) {
        T impl = null;
        synchronized(this._ref) {
            impl = this._ref.getAndSet(null);
        }
        if (null!=impl) {
            action.call(impl);
        }
    }
    
    public void runIfNotNull(final Action1<T> action) {
        synchronized(this._ref) {
            final T impl = this._ref.get();
            if (null!=impl) {
                action.call(impl);
            }
        }
    }
    
    private final AtomicReference<T> _ref;
}
