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
    
    public static final ExectionLoop immediateLoop = new ExectionLoop() {

        @Override
        public boolean inExectionLoop() {
            return true;
        }

        @Override
        public Detachable submit(final Runnable runnable) {
            runnable.run();
            return new Detachable() {
                @Override
                public void detach() {
                }};
        }

        @Override
        public Detachable schedule(final Runnable runnable, final long delayMillis) {
            runnable.run();
            return new Detachable() {
                @Override
                public void detach() {
                }};
        }};
}
