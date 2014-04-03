/**
 * 
 */
package org.jocean.idiom;

/**
 * @author isdom
 *
 */
public interface ObservationDestroyable {
    
    public interface Listener {
        
        public void onDestroyed(final ObservationDestroyable observationDestroyable) throws Exception;
    }
    
    /**
     * @return true means destroy succeed, 
     *      false means destroy failed, maybe destroy before
     */
    public boolean destroy();
    
    public boolean isDestroyed();
    
    public void registerOnDestroyedListener(final Listener listener);

    public void unregisterOnDestroyedListener(final Listener listener);

}
