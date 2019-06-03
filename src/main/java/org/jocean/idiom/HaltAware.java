package org.jocean.idiom;

import java.util.concurrent.CountDownLatch;

import rx.functions.Action0;
import rx.functions.Action1;

public interface HaltAware<T> extends Haltable {

    @Override
    public Action1<Action0> onHalt();
    public Action1<Action1<T>> onHaltOf();

    @Override
    public Action0 doOnHalt(final Action0 onhalt);
    public Action0 doOnHalt(final Action1<T> onhalt);

    public static class Util {
        public static <T extends HaltAware<?>> void awaitHalted(final T haltAware) throws InterruptedException {
            final CountDownLatch cdl = new CountDownLatch(1);
            haltAware.doOnHalt(new Action0() {
                @Override
                public void call() {
                    cdl.countDown();
                }});
            cdl.await();
        }
    }
}
