package org.jocean.idiom;

import org.jocean.idiom.rx.Action1_N;
import org.jocean.idiom.rx.RxActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

public class EndAwareSupport<T> {
    private static final Logger LOG = LoggerFactory.getLogger(EndAwareSupport.class);

    public EndAwareSupport(final InterfaceSelector selector) {
        this._registerer = selector.build(Registerer.class, ADD_ACTION_WHEN_ACTIVE, CALL_ACTION_NOW);
    }

    public void fireAllActions(final T self) {
        this._onEnds.foreachComponent(_CALL_ONEND, self);
    }

    public Action1<Action0> onEnd(final T self) {
        return new Action1<Action0>() {
            @Override
            public void call(final Action0 action) {
                doOnEnd(self, action);
            }};
    }

    public Action1<Action1<T>> onEndOf(final T self) {
        return new Action1<Action1<T>>() {
            @Override
            public void call(final Action1<T> action) {
                doOnEnd(self, action);
            }};
    }

    public Action0 doOnEnd(final T self, final Action0 onend) {
        return doOnEnd(self, RxActions.<T>toAction1(onend));
    }

    @SuppressWarnings("unchecked")
    public Action0 doOnEnd(final T self, final Action1<T> onend) {
        return this._registerer.register(this, self, (Action1<Object>) onend);
    }

    public int onEndCount() {
        return this._onEnds.componentCount();
    }

    protected interface Registerer {
        public Action0 register(final EndAwareSupport<?> support, final Object self, final Action1<Object> onend);
    }

    private static final Registerer ADD_ACTION_WHEN_ACTIVE = new Registerer() {
        @Override
        public Action0 register(final EndAwareSupport<?> support, final Object self, final Action1<Object> onend) {
            support._onEnds.addComponent(onend);
            return new Action0() {
                @Override
                public void call() {
                    support._onEnds.removeComponent(onend);
                }};
        }};

    private static final Registerer CALL_ACTION_NOW = new Registerer() {
        @Override
        public Action0 register(final EndAwareSupport<?> support, final Object self, final Action1<Object> onend) {
            onend.call(self);
            return Actions.empty();
        }};

    private static final Action1_N<Action1<Object>> _CALL_ONEND =
        new Action1_N<Action1<Object>>() {
            @Override
            public void call(final Action1<Object> onend, final Object...args) {
                try {
                    onend.call(args[0]);
                } catch (final Exception e) {
                    LOG.warn("exception when ({}) invoke onEnd({}), detail: {}", args[0], onend,
                            ExceptionUtils.exception2detail(e));
                }
            }};

    private final Registerer _registerer;

    private final COWCompositeSupport<Action1<Object>> _onEnds = new COWCompositeSupport<>();
}
