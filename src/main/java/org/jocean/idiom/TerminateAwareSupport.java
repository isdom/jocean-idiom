package org.jocean.idiom;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.Func1_N;
import org.jocean.idiom.rx.RxActions;
import org.jocean.idiom.rx.RxFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func2;

public class TerminateAwareSupport<T, F> implements TerminateAware<T> {
    private static final Logger LOG =
            LoggerFactory.getLogger(TerminateAwareSupport.class);
    
    public TerminateAwareSupport(final FuncSelector<F> selector) {
        this._doAddTerminate = 
            RxFunctions.toFunc2(selector
            .callWhenActive(addOnTerminate0())
            .callWhenDestroyed(callTerminateNow()));
    }

    public void fireAllTerminates(final T self) {
        this._onTerminates.foreachComponent(_CALL_ONTERMINATE, self);
    }
    
    @Override
    public Action1<Action0> onTerminate() {
        return new Action1<Action0>() {
            @Override
            public void call(final Action0 action) {
                doOnTerminate(action);
            }};
    }
    
    @Override
    public Action1<Action1<T>> onTerminateOf() {
        return new Action1<Action1<T>>() {
            @Override
            public void call(final Action1<T> action) {
                doOnTerminate(action);
            }};
    }

    @Override
    public Action0 doOnTerminate(final Action0 onTerminate) {
        return doOnTerminate(RxActions.<T>toAction1(onTerminate));
    }
    
    @Override
    public Action0 doOnTerminate(final Action1<T> onTerminate) {
        return this._doAddTerminate.call(this, onTerminate);
    }
    
    @SuppressWarnings("unchecked")
    private Func1_N<F, Action0> addOnTerminate0() {
        return (Func1_N<F, Action0>)ADD_TERMINATE;
    }

    @SuppressWarnings("unchecked")
    private Func1_N<F, Action0> callTerminateNow() {
        return (Func1_N<F, Action0>) CALL_TERMINATE_NOW;
    }
            
    private static final Func1_N<Object, Action0> ADD_TERMINATE = 
        new Func1_N<Object, Action0>() {
            @Override
            public Action0 call(final Object t,
                    final Object... args) {
                // args[0]: TerminateAwareSupport.this
                // args[1]: Action1<T>
                final TerminateAwareSupport<?,?> support = (TerminateAwareSupport<?,?>)args[0];
                @SuppressWarnings("unchecked")
                final Action1<Object> onTerminate = (Action1<Object>)args[1];
                support._onTerminates.addComponent(onTerminate);
                return new Action0() {
                    @Override
                    public void call() {
                        support._onTerminates.removeComponent(onTerminate);
                    }};
            }};
                
    private static final Func1_N<Object, Action0> CALL_TERMINATE_NOW = 
        new Func1_N<Object, Action0>() {
            @SuppressWarnings("unchecked")
            @Override
            public Action0 call(final Object t,
                    final Object... args) {
                // args[0]: TerminateAwareSupport.this
                // args[1]: Action1<T>
                ((Action1<Object>)args[1]).call(t);
                return Actions.empty();
            }};
            
    private static final Action1_N<Action1<Object>> _CALL_ONTERMINATE = 
        new Action1_N<Action1<Object>>() {
            @Override
            public void call(final Action1<Object> onTerminate, final Object...args) {
                try {
                    onTerminate.call(args[0]);
                } catch (Exception e) {
                    LOG.warn("exception when ({}) invoke onTerminate({}), detail: {}",
                            args[0], onTerminate, ExceptionUtils.exception2detail(e));
                }
            }};

    private final Func2<TerminateAwareSupport<T,F>, Action1<T>, Action0> _doAddTerminate;
    
    private final COWCompositeSupport<Action1<Object>> _onTerminates = 
            new COWCompositeSupport<>();
}
