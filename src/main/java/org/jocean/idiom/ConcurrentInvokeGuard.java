/**
 * 
 */
package org.jocean.idiom;

import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author isdom
 *
 */
public class ConcurrentInvokeGuard {
    
    public void enter(final String errorMsg) {
        final long tid = this._currentThreadId.get();
        if ( Thread.currentThread().getId() == tid
           ||  -1 == tid ) {
            this._trigger.increment();
            if ( Thread.currentThread().getId() 
                != this._currentThreadId.get() ) {
                throw new ConcurrentModificationException(errorMsg);
            }
        }
        else {
            throw new ConcurrentModificationException(errorMsg);
        }
    }
    
    public void leave(final String errorMsg) {
        final long tid = this._currentThreadId.get();
        if ( Thread.currentThread().getId() == tid ) {
            this._trigger.decrement();
        }
        else {
            throw new IllegalStateException(errorMsg);
        }
    }
    
    private CountedTrigger _trigger = new CountedTrigger(new CountedTrigger.Reactor() {

        @Override
        public void onIncrementFromZero() {
            if ( !_currentThreadId.compareAndSet(-1, 
                    Thread.currentThread().getId()) ) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void onDecrementToZero() {
            if ( !_currentThreadId.compareAndSet(
                    Thread.currentThread().getId(), -1) ) {
                throw new IllegalStateException();
            }
        }});
    private final AtomicLong _currentThreadId = new AtomicLong(-1);
}
