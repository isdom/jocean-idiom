/**
 * 
 */
package org.jocean.idiom;

import java.util.Map;

/**
 * @author isdom
 *
 */
public interface Propertyable {
    
    public <T> T getProperty(final String key);
    
    public <T> void setProperty(final String key, final T obj);
    
    public Map<String, Object>  getProperties();
}
