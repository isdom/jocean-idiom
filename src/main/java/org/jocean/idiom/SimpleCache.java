/**
 * 
 */
package org.jocean.idiom;

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
    
    public SimpleCache(final Function<K, V> ifAbsent) {
        this._ifAbsent = ifAbsent;
    }
    
    public V get(final K key) {
        V value = this._map.get(key);
        if ( null == value ) {
            try {
                value = this._ifAbsent.apply(key);
            } catch (Exception e) {
                LOG.warn("exception when call SimpleCache's ifAbsent with key({}), detail: {}", 
                        key, ExceptionUtils.exception2detail(e));
                throw new RuntimeException(e);
            }
            final V previousValue = this._map.putIfAbsent( key, value );
            return (null == previousValue ? value : previousValue);
        }
        else {
            return  value;
        }
    }
    
    private final ConcurrentMap<K, V>  _map = 
            new ConcurrentHashMap<K, V>();
    private final Function<K, V> _ifAbsent;
}