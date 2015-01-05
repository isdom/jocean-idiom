/**
 * 
 */
package org.jocean.idiom;

/**
 * @author isdom
 *
 */
public class StopWatch {
    public long start() {
        return this._begin = System.currentTimeMillis();
    }
    
    public long stopAndRestart() {
        final long now = System.currentTimeMillis();
        final long interval = now - this._begin;
        this._begin = now;
        return interval;
    }
    
    private long _begin;
}
