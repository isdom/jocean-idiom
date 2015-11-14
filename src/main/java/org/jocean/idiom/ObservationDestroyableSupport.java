/**
 * 
 */
package org.jocean.idiom;


import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action1;

/**
 * @author isdom
 *
 */
public class ObservationDestroyableSupport implements ObservationDestroyable {

    private static final Logger LOG = 
            LoggerFactory.getLogger(ObservationDestroyableSupport.class);
    
    public ObservationDestroyableSupport(final ObservationDestroyable owner) {
        this._owner = owner;
    }
    
    /* (non-Javadoc)
     * @see org.jocean.util.ObservationDestroyable#destroy()
     */
    @Override
    public boolean destroy() {
        if ( this._isDestroyed.compareAndSet(false, true) ) {
            this._listenerSupport.foreachComponent(new Action1<Listener> () {

                @Override
                public void call(final Listener listener) {
                    try {
                        listener.onDestroyed(_owner);
                    } catch (Exception e) {
                        LOG.warn("exception when onDestroyed, detail:{}", 
                                ExceptionUtils.exception2detail(e));
                    }
                }});
            this._listenerSupport.clear();
            return  true;
        }
        else {
            return  false;
        }
        
    }

    /* (non-Javadoc)
     * @see org.jocean.util.ObservationDestroyable#isDestroyed()
     */
    @Override
    public boolean isDestroyed() {
        return this._isDestroyed.get();
    }

    /* (non-Javadoc)
     * @see org.jocean.util.ObservationDestroyable#registerOnDestroyedListener(org.jocean.util.ObservationDestroyable.Listener)
     */
    @Override
    public void registerOnDestroyedListener(final Listener listener) {
        if ( null == listener ) {
            LOG.warn("registerOnDestroyedListener: listener is null, just ignore");
        }
        else {
            if ( !_listenerSupport.addComponent(listener) ) {
                LOG.warn("registerOnDestroyedListener: listener {} has already registered", 
                        listener);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.jocean.util.ObservationDestroyable#unregisterOnDestroyedListener(org.jocean.util.ObservationDestroyable.Listener)
     */
    @Override
    public void unregisterOnDestroyedListener(final Listener listener) {
        if ( null == listener ) {
            LOG.warn("unregisterOnDestroyedListener: listener is null, just ignore");
        }
        else {
            _listenerSupport.removeComponent(listener);
        }
    }

    private final ObservationDestroyable    _owner;
    private final AtomicBoolean _isDestroyed = new AtomicBoolean(false);
    
    private final COWCompositeSupport<Listener> _listenerSupport
                    = new COWCompositeSupport<Listener>();
}
