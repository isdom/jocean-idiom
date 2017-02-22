package org.jocean.idiom;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.RxActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action0;
import rx.functions.Action1;

public class TerminateAwareSupport<T, F> implements TerminateAware<T> {
    private static final Logger LOG =
            LoggerFactory.getLogger(TerminateAwareSupport.class);
    
    public TerminateAwareSupport(final T self, final FuncSelector<F> selector) {
        this._self = self;
        this._selector = selector;
        this._doAddTerminate = 
            RxActions.toAction1(this._selector
            .submitWhenActive(ADD_TERMINATE)
            .submitWhenDestroyed(CALL_TERMINATE_NOW));
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
    
    private final Action1_N<F> REMOVE_TERMINATE = 
        new Action1_N<F>() {
            @SuppressWarnings("unchecked")
            @Override
            public void call(final F t,
                    final Object... args) {
                _onTerminates.removeComponent((Action1<T>)args[0]);
            }};
                
    @Override
    public Action0 doOnTerminate(final Action1<T> onTerminate) {
        this._doAddTerminate.call(onTerminate);
        return new Action0() {
            @Override
            public void call() {
                _selector.submitWhenActive(REMOVE_TERMINATE).call(onTerminate);
            }};
    }
    
    private final Action1_N<F> ADD_TERMINATE = 
        new Action1_N<F>() {
            @SuppressWarnings("unchecked")
            @Override
            public void call(final F t,
                    final Object... args) {
                _onTerminates.addComponent((Action1<T>)args[0]);
            }};
                
    private final Action1_N<F> CALL_TERMINATE_NOW = 
        new Action1_N<F>() {
            @SuppressWarnings("unchecked")
            @Override
            public void call(final F t,
                    final Object... args) {
                ((Action1<T>)args[0]).call(_self);
            }};

    private final Action1<Action1<T>> _doAddTerminate;
    
    public void fireAllTerminates() {
        this._onTerminates.foreachComponent(this._callOnTerminate);
    }

    private final Action1<Action1<T>> _callOnTerminate = 
        new Action1<Action1<T>>() {
            @Override
            public void call(final Action1<T> onTerminate) {
                try {
                    onTerminate.call(_self);
                } catch (Exception e) {
                    LOG.warn("exception when ({}) invoke onTerminate({}), detail: {}",
                            _self, onTerminate, ExceptionUtils.exception2detail(e));
                }
            }};

    private final COWCompositeSupport<Action1<T>> _onTerminates = 
            new COWCompositeSupport<>();
    
    private final T _self;
    private final FuncSelector<F> _selector;
}
