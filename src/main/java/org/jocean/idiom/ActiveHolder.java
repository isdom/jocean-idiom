package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.Func1_N;

import rx.functions.Action1;
import rx.functions.ActionN;
import rx.functions.FuncN;

public class ActiveHolder<T> {
    
    public interface SubmitSuccessor extends ActionN {
        public ActionN submitWhenDestroyed(final ActionN actionWhenDestroyed);
    }
    
    public interface CallSuccessor<R> extends FuncN<R> {
        public FuncN<R> callWhenDestroyed(final FuncN<R> funcWhenDestroyed);
    }
    
    public ActiveHolder(final T data) {
        this._data = new AtomicReference<T>(data);
    }
    
    public boolean isActive() {
        return this._data.get() != null;
    }
    
    public void destroy(final Action1<T> actionWhenDestroying) {
        T data = null;
        synchronized(this._data) {
            data = this._data.getAndSet(null);
        }
        if (null!=data && null!=actionWhenDestroying) {
            actionWhenDestroying.call(data);
        }
    }
    
    public SubmitSuccessor submitWhenActive(final Action1_N<T> actionWhenActive) {
        return new SubmitSuccessor() {
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
            public ActionN submitWhenDestroyed(final ActionN actionWhenDestroyed) {
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
                            actionWhenDestroyed.call(args);
                        }
                    }};
            }};
            
    }
    
    public <R> CallSuccessor<R> callWhenActive(final Func1_N<T, R> funcWhenActive) {
        return new CallSuccessor<R>() {

            @Override
            public R call(final Object... args) {
                synchronized(_data) {
                    final T data = _data.get();
                    return (null!=data) ? funcWhenActive.call(data, args) : null;
                }
            }

            @Override
            public FuncN<R> callWhenDestroyed(final FuncN<R> funcWhenDestroyed) {
                return new FuncN<R>() {

                    @Override
                    public R call(final Object... args) {
                        synchronized(_data) {
                            final T data = _data.get();
                            if (null!=data) {
                                return funcWhenActive.call(data, args) ;
                            }
                        }
                        return funcWhenDestroyed.call(args);
                    }};
            }};
    }
    
    private final AtomicReference<T> _data;
}
