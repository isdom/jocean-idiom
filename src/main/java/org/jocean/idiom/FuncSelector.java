package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import rx.functions.ActionN;
import rx.functions.FuncN;

public class FuncSelector {
    
    public interface SubmitSuccessor extends ActionN {
        public ActionN submitWhenDestroyed(final ActionN actionWhenDestroyed);
    }
    
    public interface CallSuccessor<R> extends FuncN<R> {
        public FuncN<R> callWhenDestroyed(final FuncN<R> funcWhenDestroyed);
    }
    
    public boolean isActive() {
        return 1 == updater.get(this);
    }
    
    public void destroy(final ActionN actionWhenDestroying, final Object... args) {
        boolean actived;
        synchronized(this) {
            actived = updater.compareAndSet(this, 1, 0);
        }
        if (actived && null!=actionWhenDestroying) {
            actionWhenDestroying.call(args);
        }
    }
    
    public SubmitSuccessor submitWhenActive(final ActionN actionWhenActive) {
        return new SubmitSuccessor() {
            @Override
            public void call(final Object... args) {
                synchronized(FuncSelector.this) {
                    if (isActive() && null!=actionWhenActive) {
                        actionWhenActive.call(args);
                    }
                }
            }
            
            @Override
            public ActionN submitWhenDestroyed(final ActionN actionWhenDestroyed) {
                return new ActionN() {
                    @Override
                    public void call(final Object... args) {
                        synchronized(FuncSelector.this) {
                            if (isActive()) {
                                if (null!=actionWhenActive) {
                                    actionWhenActive.call(args);
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
    
    public <R> CallSuccessor<R> callWhenActive(final FuncN<R> funcWhenActive) {
        return new CallSuccessor<R>() {

            @Override
            public R call(final Object... args) {
                synchronized(FuncSelector.this) {
                    return (isActive() && null!=funcWhenActive) ? funcWhenActive.call(args) : null;
                }
            }

            @Override
            public FuncN<R> callWhenDestroyed(final FuncN<R> funcWhenDestroyed) {
                return new FuncN<R>() {
                    @Override
                    public R call(final Object... args) {
                        synchronized(FuncSelector.this) {
                            if (isActive()) {
                                return null != funcWhenActive ? funcWhenActive.call(args) : null;
                            }
                        }
                        return funcWhenDestroyed.call(args);
                    }};
            }};
    }
    
    private static final AtomicIntegerFieldUpdater<FuncSelector> updater =
            AtomicIntegerFieldUpdater.newUpdater(FuncSelector.class, "_state");

    @SuppressWarnings("unused")
    private volatile int _state = 1;
}
