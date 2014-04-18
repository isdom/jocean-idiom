/**
 * 
 */
package org.jocean.idiom;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author isdom
 *
 */
public class SimpleCache<K, V> {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(SimpleCache.class);
    
    public V get(final K key, final Function<K, V> ifAbsent) {
        V value = this._map.get(key);
        if ( null == value ) {
            try {
                value = ifAbsent.apply(key);
            } catch (Exception e) {
                LOG.warn("exception when call SimpleCache's ifAbsent with key({}), detail: {}", 
                        key, ExceptionUtils.exception2detail(e));
                throw new RuntimeException(e);
            }
            final V oldValue = this._map.putIfAbsent( key, value );
            return (null == oldValue ? value : oldValue);
        }
        else {
            return  value;
        }
    }
    
    private final ConcurrentMap<K, V>  _map = new ConcurrentHashMap<K, V>();
}