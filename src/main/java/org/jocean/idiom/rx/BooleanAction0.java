package org.jocean.idiom.rx;

import java.util.concurrent.atomic.AtomicReference;

import rx.functions.Action0;

public final class BooleanAction0 implements Action0 {

    final AtomicReference<Action0> actionRef;

    public BooleanAction0() {
        actionRef = new AtomicReference<Action0>();
    }

    private BooleanAction0(Action0 action) {
        actionRef = new AtomicReference<Action0>(action);
    }

    /**
     * Creates a {@code BooleanAction0} without unsubscribe behavior.
     *
     * @return the created {@code BooleanSubscription}
     */
    public static BooleanAction0 create() {
        return new BooleanAction0();
    }

    /**
     * Creates a {@code BooleanAction0} with a specified function to invoke upon call.
     *
     * @param actual
     *          an {@link Action0} to invoke upon call
     * @return the created {@code BooleanAction0}
     */
    public static BooleanAction0 create(final Action0 actual) {
        return new BooleanAction0(actual);
    }

    @Override
    public final void call() {
        Action0 action = actionRef.get();
        if (action != EMPTY_ACTION) {
            action = actionRef.getAndSet(EMPTY_ACTION);
            if (action != null && action != EMPTY_ACTION) {
                action.call();
            }
        }
    }

    static final Action0 EMPTY_ACTION = new Action0() {
        @Override
        public void call() {

        }
    };
}
