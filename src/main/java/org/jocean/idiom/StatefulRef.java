package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.Func1_N;

import rx.functions.Action1;
import rx.functions.ActionN;
import rx.functions.FuncN;

public class StatefulRef<T> {

    @SuppressWarnings("unchecked")
    public static <E> E getArgAs(final int idx, final Object... args) {
        if (null!=args && idx >= 0 && idx < args.length) {
            return (E)args[idx];
        } else {
            throw new RuntimeException("invalid params");
        }
    }
    
    public interface SubmitSuccessor extends ActionN {
        public ActionN submitWhenDestroyed(final ActionN actionWhenDestroyed);
    }
    
    public interface CallSuccessor<R> extends FuncN<R> {
        public FuncN<R> callWhenDestroyed(final FuncN<R> funcWhenDestroyed);
    }
    
    public StatefulRef(final T impl) {
        this._ref = new AtomicReference<T>(impl);
    }
    
    public void destroy(final Action1<T> actionWhenDestroying) {
        T impl = null;
        synchronized(this._ref) {
            impl = this._ref.getAndSet(null);
        }
        if (null!=impl && null!=actionWhenDestroying) {
            actionWhenDestroying.call(impl);
        }
    }
    
    public SubmitSuccessor submitWhenActive(final Action1_N<T> actionWhenActive) {
        return new SubmitSuccessor() {
            @Override
            public void call(final Object... args) {
                synchronized(_ref) {
                    final T impl = _ref.get();
                    if (null!=impl && null!=actionWhenActive) {
                        actionWhenActive.call(impl, args);
                    }
                }
            }
            @Override
            public ActionN submitWhenDestroyed(final ActionN actionWhenDestroyed) {
                return new ActionN() {
                    @Override
                    public void call(final Object... args) {
                        synchronized(_ref) {
                            final T impl = _ref.get();
                            if (null!=impl) {
                                if (null!=actionWhenActive) {
                                    actionWhenActive.call(impl, args);
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
                synchronized(_ref) {
                    final T impl = _ref.get();
                    return (null!=impl) ? funcWhenActive.call(impl, args) : null;
                }
            }

            @Override
            public FuncN<R> callWhenDestroyed(final FuncN<R> funcWhenDestroyed) {
                return new FuncN<R>() {

                    @Override
                    public R call(final Object... args) {
                        synchronized(_ref) {
                            final T impl = _ref.get();
                            return (null!=impl) 
                                ? funcWhenActive.call(impl, args) 
                                : funcWhenDestroyed.call(args);
                        }
                    }};
            }};
    }
    
    private final AtomicReference<T> _ref;
}
