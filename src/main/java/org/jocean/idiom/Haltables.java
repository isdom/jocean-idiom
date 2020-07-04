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

    public final static HaltableFactory DELAY_30S = new HaltableFactory() {
        @Override
        public Haltable build() {
            return delay(30, TimeUnit.SECONDS);
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

    public final static HaltableTransitiveFactory EXTEND_30S = new HaltableTransitiveFactory() {
        @Override
        public Haltable build(final Haltable haltable) {
            return delay(30, TimeUnit.SECONDS);
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
