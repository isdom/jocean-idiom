package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.Func1_N;

import rx.functions.ActionN;
import rx.functions.FuncN;

public class FuncSelector<T> {
    
    public interface SubmitSuccessor<T> extends ActionN {
        public ActionN submitWhenDestroyed(final Action1_N<T> actionWhenDestroyed);
    }
    
    public interface CallSuccessor<T,R> extends FuncN<R> {
        public FuncN<R> callWhenDestroyed(final Func1_N<T,R> funcWhenDestroyed);
    }
    
    public FuncSelector(final T data) {
        this._data = new AtomicReference<T>(data);
    }
    
    public boolean isActive() {
        return this._data.get() != null;
    }
    
    public void destroy(final Action1_N<T> actionWhenDestroying, final Object... args) {
        T data = null;
        synchronized(this._data) {
            data = this._data.getAndSet(null);
            if (null!=data) {
                this._destroyed = data;
            }
        }
        if (null!=data && null!=actionWhenDestroying) {
            actionWhenDestroying.call(data, args);
        }
    }
    
    public SubmitSuccessor<T> submitWhenActive(final Action1_N<T> actionWhenActive) {
        return new SubmitSuccessor<T>() {
            @Override
            public void call(final Object... args) {
                synchronized(_data) {
                    final T data = _data.get();
                    if (null!=data && null!=actionWhenActive) {
                        actionWhenActive.call(data, args);
                    }
                }
            }
            
            @Override
            public ActionN submitWhenDestroyed(final Action1_N<T> actionWhenDestroyed) {
                return new ActionN() {
                    @Override
                    public void call(final Object... args) {
                        synchronized(_data) {
                            final T data = _data.get();
                            if (null!=data) {
                                if (null!=actionWhenActive) {
                                    actionWhenActive.call(data, args);
                                }
                                return;
                            }
                        }
                        if (null!=actionWhenDestroyed) {
                            actionWhenDestroyed.call(_destroyed, args);
                        }
                    }};
            }};
            
    }
    
    public <R> CallSuccessor<T,R> callWhenActive(final Func1_N<T, R> funcWhenActive) {
        return new CallSuccessor<T,R>() {

            @Override
            public R call(final Object... args) {
                synchronized(_data) {
                    final T data = _data.get();
                    return (null!=data) ? funcWhenActive.call(data, args) : null;
                }
            }

            @Override
            public FuncN<R> callWhenDestroyed(final Func1_N<T,R> funcWhenDestroyed) {
                return new FuncN<R>() {

                    @Override
                    public R call(final Object... args) {
                        synchronized(_data) {
                            final T data = _data.get();
                            if (null!=data) {
                                return funcWhenActive.call(data, args) ;
                            }
                        }
                        return funcWhenDestroyed.call(_destroyed, args);
                    }};
            }};
    }
    
    private final AtomicReference<T> _data;
    private volatile T _destroyed;
}
