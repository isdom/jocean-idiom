/**
 * 
 */
package org.jocean.idiom;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action2;
import rx.functions.Func1;

/**
 * @author isdom
 *
 */
public class SimpleCache<K, V> {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(SimpleCache.class);
    
    public SimpleCache(final Func1<K, V> ifAbsent) {
        this(ifAbsent, null);
    }
    
    public SimpleCache(final Func1<K, V> ifAbsent, final Action2<K,V> ifAssociated) {
        this._ifAbsent = ifAbsent;
        this._ifAssociated = ifAssociated;
    }
    
    public V get(final K key) {
        V value = this._map.get(key);
        if ( null == value ) {
            try {
                value = this._ifAbsent.call(key);
            } catch (Exception e) {
                LOG.warn("exception when call SimpleCache's ifAbsent with key({}), detail: {}", 
                        key, ExceptionUtils.exception2detail(e));
                throw new RuntimeException(e);
            }
            final V previousValue = this._map.putIfAbsent( key, value );
            if ( null == previousValue ) {
                if ( null != this._ifAssociated) {
                    try {
                        this._ifAssociated.call(key, value);
                    } catch (Exception e) {
                        LOG.warn("exception when call SimpleCache's ifAssociated with key({})/value({}), detail: {}", 
                                key, value, ExceptionUtils.exception2detail(e));
                    }
                }
                return value;
            }
            else {
                return previousValue;
            }
        }
        else {
            return  value;
        }
    }
    
    public void clear() {
        this._map.clear();
    }
    
    public Map<K, V> snapshot() {
        final Map<K, V> result = new HashMap<>();
        result.putAll(this._map);
        return result;
    }
    
    private final ConcurrentMap<K, V>  _map = 
            new ConcurrentHashMap<>();
    private final Func1<K, V> _ifAbsent;
    private final Action2<K,V> _ifAssociated;
}