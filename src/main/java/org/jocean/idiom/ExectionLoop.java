/**
 * 
 */
package org.jocean.idiom;


/**
 * @author isdom
 *
 */
public interface ExectionLoop {
    public boolean inExectionLoop();
    public Detachable submit(final Runnable runnable);
    public Detachable schedule(final Runnable runnable, final long delayMillis);
}
