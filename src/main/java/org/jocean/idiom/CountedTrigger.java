/**
 * 
 */
package org.jocean.idiom;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author isdom
 *
 */
public class CountedTrigger {
    
//    private static final Logger LOG = 
//            LoggerFactory.getLogger(CountedTrigger.class);

    public interface Reactor {
        public void onIncrementFromZero();
        public void onDecrementToZero();
    }
    
    public CountedTrigger(final Reactor reactor) {
        if ( null == reactor ) {
            throw new NullPointerException("CountedTrigger's Reactor can not be null.");
        }
        
        this._reactor = reactor;
    }

    public void increment() {
        if ( this._counter.getAndIncrement() == 0 ) {
            this._reactor.onIncrementFromZero();
        }
    }
    
    public void decrement() {
        final int count = this._counter.decrementAndGet();
        if ( count < 0 ) {
            throw new RuntimeException("Internal Error: counter.decrementAndGet()'s result < 0");
        }
        if ( count == 0 ) {
            this._reactor.onDecrementToZero();
        }
    }
    
    private final Reactor _reactor;
    private final AtomicInteger _counter = new AtomicInteger(0);
}
