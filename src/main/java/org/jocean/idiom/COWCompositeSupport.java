/**
 * 
 */
package org.jocean.idiom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author isdom
 *
 */
public class COWCompositeSupport<T> {

    private static final Logger LOG = 
        	LoggerFactory.getLogger(COWCompositeSupport.class);
    
    public boolean isEmpty() {
    	final List<T> current = this._components.get();
    	return (null == current) || current.isEmpty();
    }
    
	public boolean addComponent(final T component) {
		synchronized( this._components) {
			final List<T> current = _components.get();
			if ( null != current ) {
				if ( current.contains(component) ) {
					return	false;
				}
			}
			final List<T> newComponents = new ArrayList<T>();
			if ( null != current ) {
				newComponents.addAll(current);
			}
			newComponents.add(component);
			_components.set(newComponents);
		}
		
		return	true;
	}
	
	public void removeComponent(final T component) {
		synchronized( this._components) {
			final List<T> current = _components.get();
			if ( null != current 
				&& current.contains(component)) {
				final List<T> newComponents = new ArrayList<T>(current);
				newComponents.remove(component);
				this._components.set( 
					!newComponents.isEmpty() ? newComponents : null);
			}
		}
	}

	public void foreachComponent(final Visitor<T> visitor) {
    	final List<T> current = this._components.get();
    	if ( null != current ) {
    		for ( int idx = 0; idx < current.size(); idx++) {
    			final T component = current.get(idx);
    			try {
    				visitor.visit( component );
    			}
    			catch (Exception e) {
    				LOG.error("exception when call COWCompositeSupport.foreachComponent, detail:{}",
    						ExceptionUtils.exception2detail(e));
    			}
    		}
    	}
	}
	
	public void clear() {
		synchronized( this._components) {
			final List<T> old = _components.getAndSet(null);
			if ( null != old ) {
				old.clear();
			}
		}
	}
	
    private final AtomicReference<List<T>> 
		_components = new AtomicReference<>(null);
}
