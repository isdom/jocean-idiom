package org.jocean.idiom;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.RxActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

public class HaltAwareSupport<T> {
    private static final Logger LOG = LoggerFactory.getLogger(HaltAwareSupport.class);

    public HaltAwareSupport(final InterfaceSelector selector) {
        this._registerer = selector.build(Registerer.class, ADD_ACTION_WHEN_ACTIVE, CALL_ACTION_NOW);
    }

    public void fireAllActions(final T self) {
        this._onHalts.foreachComponent(_CALL_ONHALT, self);
    }

    public Action1<Action0> onHalt(final T self) {
        return new Action1<Action0>() {
            @Override
            public void call(final Action0 action) {
                doOnHalt(self, action);
            }};
    }

    public Action1<Action1<T>> onHaltOf(final T self) {
        return new Action1<Action1<T>>() {
            @Override
            public void call(final Action1<T> action) {
                doOnHalt(self, action);
            }};
    }

    public Action0 doOnHalt(final T self, final Action0 onhalt) {
        return doOnHalt(self, RxActions.<T>toAction1(onhalt));
    }

    @SuppressWarnings("unchecked")
    public Action0 doOnHalt(final T self, final Action1<T> onhalt) {
        return this._registerer.register(this, self, (Action1<Object>) onhalt);
    }

    public int onHaltCount() {
        return this._onHalts.componentCount();
    }

    protected interface Registerer {
        public Action0 register(final HaltAwareSupport<?> support, final Object self, final Action1<Object> onhalt);
    }

    private static final Registerer ADD_ACTION_WHEN_ACTIVE = new Registerer() {
        @Override
        public Action0 register(final HaltAwareSupport<?> support, final Object self, final Action1<Object> onhalt) {
            support._onHalts.addComponent(onhalt);
            return new Action0() {
                @Override
                public void call() {
                    support._onHalts.removeComponent(onhalt);
                }};
        }};

    private static final Registerer CALL_ACTION_NOW = new Registerer() {
        @Override
        public Action0 register(final HaltAwareSupport<?> support, final Object self, final Action1<Object> onend) {
            onend.call(self);
            return Actions.empty();
        }};

    private static final Action1_N<Action1<Object>> _CALL_ONHALT =
        new Action1_N<Action1<Object>>() {
            @Override
            public void call(final Action1<Object> onhalt, final Object...args) {
                try {
                    onhalt.call(args[0]);
                } catch (final Exception e) {
                    LOG.warn("exception when ({}) invoke onHalt({}), detail: {}", args[0], onhalt,
                            ExceptionUtils.exception2detail(e));
                }
            }};

    private final Registerer _registerer;

    private final COWCompositeSupport<Action1<Object>> _onHalts = new COWCompositeSupport<>();
}
