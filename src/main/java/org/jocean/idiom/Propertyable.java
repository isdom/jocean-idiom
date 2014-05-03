/**
 * 
 */
package org.jocean.idiom;

import java.util.Map;

/**
 * @author isdom
 *
 */
public interface Propertyable<T extends Propertyable<?>> {
    
    public <V> V getProperty(final String key);
    
    public <V> T setProperty(final String key, final V obj);
    
    public Map<String, Object>  getProperties();
}
