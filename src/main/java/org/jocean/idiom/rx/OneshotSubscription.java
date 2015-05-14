package org.jocean.idiom.rx;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jocean.idiom.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Subscription;

public abstract class OneshotSubscription implements Subscription {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(OneshotSubscription.class);
    
    protected abstract void doUnsubscribe();
    
    @Override
    public void unsubscribe() {
        if (isUnsubscribed.compareAndSet(false, true)) {
            try {
                doUnsubscribe();
            } catch (final Throwable e) {
                LOG.warn("exception when doUnsubscribe, detail:{}", 
                        ExceptionUtils.exception2detail(e));
            }
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return isUnsubscribed.get();
    }

    final AtomicBoolean isUnsubscribed = new AtomicBoolean(false);
}
