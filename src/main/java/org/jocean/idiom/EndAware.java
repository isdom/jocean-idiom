package org.jocean.idiom;

import java.util.concurrent.CountDownLatch;

import rx.functions.Action0;
import rx.functions.Action1;

public interface EndAware<T> extends Endable {

    @Override
    public Action1<Action0> onEnd();
    public Action1<Action1<T>> onEndOf();

    @Override
    public Action0 doOnEnd(final Action0 onend);
    public Action0 doOnEnd(final Action1<T> onend);

    public static class Util {
        public static <T extends EndAware<?>> void awaitEnded(final T endAware) throws InterruptedException {
            final CountDownLatch cdl = new CountDownLatch(1);
            endAware.doOnEnd(new Action0() {
                @Override
                public void call() {
                    cdl.countDown();
                }});
            cdl.await();
        }
    }
}
