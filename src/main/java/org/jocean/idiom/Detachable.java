/**
 * 
 */
package org.jocean.idiom;

/**
 * @author isdom
 *
 */
public interface Detachable {
	public void detach() throws Exception;
	
    public static Detachable DoNothing = new Detachable() {
        @Override
        public void detach() throws Exception {
        }};
}
