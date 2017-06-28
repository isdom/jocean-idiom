package org.jocean.idiom;

import java.util.concurrent.CountDownLatch;

import rx.functions.Action0;
import rx.functions.Action1;

public interface TerminateAware<T> {
    
    public Action1<Action0> onTerminate();
    public Action1<Action1<T>> onTerminateOf();
    
    public Action0 doOnTerminate(final Action0 onTerminate);
    public Action0 doOnTerminate(final Action1<T> onTerminate);
    
    public static class Util {
        public static <T extends TerminateAware<?>> void awaitTerminated(final T terminateAware) 
                throws InterruptedException {
            final CountDownLatch cdl = new CountDownLatch(1);
            terminateAware.doOnTerminate(new Action0() {
                @Override
                public void call() {
                    cdl.countDown();
                }});
            cdl.await();
        }
    }
}
