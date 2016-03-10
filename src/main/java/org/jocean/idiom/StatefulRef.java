package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicReference;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public class StatefulRef<T> {

    public interface SubmitSuccessor extends Action0 {
        public Action0 submitWhenDestroyed(final Action0 actionWhenDestroyed);
    }
    
    public interface CallSuccessor<R> extends Func0<R> {
        public Func0<R> callWhenDestroyed(final Func0<R> funcWhenDestroyed);
    }
    
    public StatefulRef(final T impl) {
        this._ref = new AtomicReference<T>(impl);
    }
    
    public void destroy(final Action1<T> actionWhenDestroying) {
        T impl = null;
        synchronized(this._ref) {
            impl = this._ref.getAndSet(null);
        }
        if (null!=impl) {
            actionWhenDestroying.call(impl);
        }
    }
    
    public SubmitSuccessor submitWhenActive(final Action1<T> actionWhenActive) {
        return new SubmitSuccessor() {
            @Override
            public void call() {
                synchronized(_ref) {
                    final T impl = _ref.get();
                    if (null!=impl) {
                        actionWhenActive.call(impl);
                    }
                }
            }
            @Override
            public Action0 submitWhenDestroyed(final Action0 actionWhenDestroyed) {
                return new Action0() {
                    @Override
                    public void call() {
                        synchronized(_ref) {
                            final T impl = _ref.get();
                            if (null!=impl) {
                                actionWhenActive.call(impl);
                                return;
                            }
                        }
                        actionWhenDestroyed.call();
                    }};
            }};
            
    }
    
    public <R> CallSuccessor<R> callWhenActive(final Func1<T, R> funcWhenActive) {
        return new CallSuccessor<R>() {

            @Override
            public R call() {
                synchronized(_ref) {
                    final T impl = _ref.get();
                    return (null!=impl) ? funcWhenActive.call(impl) : null;
                }
            }

            @Override
            public Func0<R> callWhenDestroyed(final Func0<R> funcWhenDestroyed) {
                return new Func0<R>() {

                    @Override
                    public R call() {
                        synchronized(_ref) {
                            final T impl = _ref.get();
                            return (null!=impl) ? funcWhenActive.call(impl) : funcWhenDestroyed.call();
                        }
                    }};
            }};
    }
    
    private final AtomicReference<T> _ref;
}
