package org.jocean.idiom;

import java.util.concurrent.TimeUnit;

import org.jocean.idiom.rx.RxActions;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class Haltables {

    private Haltables() {
        throw new IllegalStateException("No instances!");
    }

    public final static HaltableBuilder DELAY_30S = new HaltableBuilder() {
        @Override
        public Haltable build() {
            return delay(30, TimeUnit.SECONDS);
        }};

    public final static HaltableBuilder DELAY_20S = new HaltableBuilder() {
        @Override
        public Haltable build() {
            return delay(20, TimeUnit.SECONDS);
        }};

    public final static HaltableBuilder DELAY_10S = new HaltableBuilder() {
        @Override
        public Haltable build() {
            return delay(10, TimeUnit.SECONDS);
        }};

    public final static Haltable NEVER = new Haltable() {
        @Override
        public Action1<Action0> onHalt() {
            return Actions.empty();
        }
        @Override
        public Action0 doOnHalt(final Action0 onhalt) {
            return Actions.empty();
    }};

    public final static HaltableRelyBuilder EXTEND_10S = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 10, TimeUnit.SECONDS);
        }};

    public final static HaltableRelyBuilder EXTEND_20S = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 20, TimeUnit.SECONDS);
        }};

    public final static HaltableRelyBuilder EXTEND_30S = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 30, TimeUnit.SECONDS);
        }};

    public final static HaltableRelyBuilder EXTEND_1MINUTES = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 1, TimeUnit.MINUTES);
        }};

    public final static HaltableRelyBuilder EXTEND_5MINUTES = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 5, TimeUnit.MINUTES);
        }};

    public final static HaltableRelyBuilder EXTEND_10MINUTES = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 10, TimeUnit.MINUTES);
        }};

    public final static HaltableRelyBuilder EXTEND_15MINUTES = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 15, TimeUnit.MINUTES);
        }};

    public final static HaltableRelyBuilder EXTEND_30MINUTES = new HaltableRelyBuilder() {
        @Override
        public Haltable build(final Haltable haltable) {
            return extend(haltable, 30, TimeUnit.MINUTES);
        }};
    public static Haltable nop() {
        return new Haltable() {

            @Override
            public Action1<Action0> onHalt() {
                return Actions.empty();
            }

            @Override
            public Action0 doOnHalt(final Action0 onend) {
                return Actions.empty();
            }};
    }

    public static Haltable delay(final long delay, final TimeUnit unit) {
        final CompositeSubscription cs = new CompositeSubscription();

        Observable.timer(delay, unit).subscribe(RxActions.toAction1(RxActions.subscription2Action0(cs)));

        return cs2haltable(cs);
    }

    public static Haltable extend(final Haltable haltable, final long delay, final TimeUnit unit) {
        final CompositeSubscription cs = new CompositeSubscription();

        haltable.doOnHalt(()->Observable.timer(delay, unit).subscribe(RxActions.toAction1(RxActions.subscription2Action0(cs))));

        return cs2haltable(cs);
    }

    private static Haltable cs2haltable(final CompositeSubscription cs) {
        return new Haltable() {
            @Override
            public Action1<Action0> onHalt() {
                return new Action1<Action0>() {
                    @Override
                    public void call(final Action0 onhalt) {
                        doOnHalt(onhalt);
                    }};
            }

            @Override
            public Action0 doOnHalt(final Action0 onhalt) {
                final Subscription s = Subscriptions.create(onhalt);
                cs.add(s);
                return new Action0() {
                    @Override
                    public void call() {
                        cs.remove(s);
                    }};
            }};
    }
}
