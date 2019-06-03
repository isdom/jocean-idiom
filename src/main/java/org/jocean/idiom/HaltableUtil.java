/**
 *
 */
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

/**
 * @author isdom
 *
 */
public class HaltableUtil {

    private HaltableUtil() {
        throw new IllegalStateException("No instances!");
    }

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
