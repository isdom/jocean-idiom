package org.jocean.idiom;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.RxActions;
import org.jocean.idiom.rx.RxFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func3;
import rx.functions.FuncN;

public class TerminateAwareSupport<T> {
    private static final Logger LOG =
            LoggerFactory.getLogger(TerminateAwareSupport.class);
    
    public TerminateAwareSupport(final FuncSelector selector) {
        this._doAddTerminate = 
            RxFunctions.toFunc3(selector
            .callWhenActive(ADD_TERMINATE)
            .callWhenDestroyed(CALL_TERMINATE_NOW));
    }

    public void fireAllTerminates(final T self) {
        this._onTerminates.foreachComponent(_CALL_ONTERMINATE, self);
    }
    
    public Action1<Action0> onTerminate(final T self) {
        return new Action1<Action0>() {
            @Override
            public void call(final Action0 action) {
                doOnTerminate(self, action);
            }};
    }
    
    public Action1<Action1<T>> onTerminateOf(final T self) {
        return new Action1<Action1<T>>() {
            @Override
            public void call(final Action1<T> action) {
                doOnTerminate(self, action);
            }};
    }

    public Action0 doOnTerminate(final T self, final Action0 onTerminate) {
        return doOnTerminate(self, RxActions.<T>toAction1(onTerminate));
    }
    
    public Action0 doOnTerminate(final T self, final Action1<T> onTerminate) {
        return this._doAddTerminate.call(self, this, onTerminate);
    }
    
    private static final FuncN<Action0> ADD_TERMINATE = 
        new FuncN<Action0>() {
            @Override
            public Action0 call(final Object... args) {
                // args[0]: T self
                // args[1]: TerminateAwareSupport.this
                // args[2]: Action1<T> onTerminate
                final TerminateAwareSupport<?> support = (TerminateAwareSupport<?>)args[1];
                @SuppressWarnings("unchecked")
                final Action1<Object> onTerminate = (Action1<Object>)args[2];
                support._onTerminates.addComponent(onTerminate);
                return new Action0() {
                    @Override
                    public void call() {
                        support._onTerminates.removeComponent(onTerminate);
                    }};
            }};
                
    private static final FuncN<Action0> CALL_TERMINATE_NOW = 
        new FuncN<Action0>() {
            @SuppressWarnings("unchecked")
            @Override
            public Action0 call(final Object... args) {
                // args[0]: T self
                // args[1]: TerminateAwareSupport.this
                // args[2]: Action1<T> onTerminate
                ((Action1<Object>)args[2]).call(args[0]);
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

    private final Func3<T, TerminateAwareSupport<T>, Action1<T>, Action0> _doAddTerminate;
    
    private final COWCompositeSupport<Action1<Object>> _onTerminates = 
            new COWCompositeSupport<>();
}
