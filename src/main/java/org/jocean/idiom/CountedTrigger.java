/**
 * 
 */
package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author isdom
 *
 */
public class CountedTrigger {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(CountedTrigger.class);

    public interface Reactor {
        public void onIncrementFromZero() throws Exception;
        public void onDecrementToZero() throws Exception;
    }
    
    public CountedTrigger(final Reactor reactor) {
        if ( null == reactor ) {
            throw new NullPointerException("CountedTrigger's Reactor can not be null.");
        }
        
        this._reactor = reactor;
    }

    public void increment() {
        if ( this._counter.getAndIncrement() == 0 ) {
            try {
                this._reactor.onIncrementFromZero();
            }
            catch (Exception e) {
                LOG.warn("exception when Reactor.onIncrementFromZero, detail:{}",
                        ExceptionUtils.exception2detail(e));
            }
        }
    }
    
    public void decrement() {
        final int count = this._counter.decrementAndGet();
        if ( count < 0 ) {
            throw new RuntimeException("Internal Error: _counter.decrementAndGet()'s result < 0");
        }
        if ( count == 0 ) {
            try {
                this._reactor.onDecrementToZero();
            }
            catch (Exception e) {
                LOG.warn("exception when Reactor.onDecrementToZero, detail:{}",
                        ExceptionUtils.exception2detail(e));
            }
        }
    }
    
    private final Reactor _reactor;
    private final AtomicInteger _counter = new AtomicInteger(0);
}
