package org.jocean.idiom.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.AbstractReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(AbstractObjectPool.class);

    public Ref<T> retainObject() {
        final T obj = this._idles.poll();
        if ( null != obj ) {
            final int idleCount = this._idleCounter.decrementAndGet();
            final int retainedCount = addToRetained(obj);
            if ( LOG.isTraceEnabled() ) {
                LOG.trace("AbstractObjectPool: retainObject({}) succeed, now idle {}/retained {}", 
                        obj.getClass(), idleCount, retainedCount);
            }
            return new RefImpl(obj);
        }
        else {
            final T newobj = createObject();
            if ( null == newobj ) {
                LOG.trace("AbstractObjectPool: createObject return null, current retained {} and idles {}",
                        this._retainedCounter.get(), this._idleCounter.get());
                throw new IllegalStateException("AbstractObjectPool: createObject return null");
            }
            final int retainedCount = addToRetained(newobj);
            if ( LOG.isTraceEnabled() ) {
                LOG.trace("AbstractObjectPool: retainObject({}) succeed, now idle {}/retained {}", 
                        newobj.getClass(), this._idleCounter.get(), retainedCount);
            }
            return new RefImpl(newobj);
        }
    }

    /**
     * @param newobj
     */
    private int addToRetained(final T newobj) {
        if ( this._retains.offer(newobj) ) {
            final int objCount = this._retainedCounter.incrementAndGet();
            return objCount;
        }
        else {
            return this._retainedCounter.get();
        }
    }
    
    /**
     * @param newobj
     */
    private int removeFromRetained(final T obj) {
        if ( this._retains.remove(obj) ) {
            final int objCount = this._retainedCounter.decrementAndGet();
            return objCount;
        }
        else {
            return this._retainedCounter.get();
        }
    }
    
    public int getIdleCount() {
        return this._idleCounter.get();
    }
    
    public int getRetainedCount() {
        return this._retainedCounter.get();
    }
    
    private final class RefImpl 
        extends AbstractReferenceCounted<Ref<T>>
        implements Ref<T> {

        RefImpl(final T obj) {
            this._obj.set(obj);
        }
        
        @Override
        protected void deallocate() {
            AbstractObjectPool.this.returnObject(this._obj.getAndSet(null));
        }

        @Override
        public T object() {
            return this._obj.get();
        }
        
        private final AtomicReference<T> _obj = new AtomicReference<T>(null);
    }
    
    private void returnObject(final T obj) {
        if ( this._idles.offer(obj) ) {
            final int idleCount = this._idleCounter.incrementAndGet();
            final int retainedCount = removeFromRetained(obj);
            if ( LOG.isTraceEnabled() ) {
                LOG.trace("AbstractObjectPool: returnObject({}) succeed, now idle {}/retained {}", 
                        obj.getClass(), idleCount, retainedCount);
            }
        }
    }

    protected abstract T createObject();
    
    protected final Queue<T> _idles = new ConcurrentLinkedQueue<T>();
    protected final Queue<T> _retains = new ConcurrentLinkedQueue<T>();
    protected final AtomicInteger _idleCounter = new AtomicInteger(0);
    protected final AtomicInteger _retainedCounter = new AtomicInteger(0);
}
