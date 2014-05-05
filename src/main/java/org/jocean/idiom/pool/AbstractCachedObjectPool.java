package org.jocean.idiom.pool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.AbstractReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCachedObjectPool<T> implements CachedObjectPool<T> {
    
    private final Logger LOG;

    protected AbstractCachedObjectPool(final Logger logger) {
        LOG = null != logger ? logger : LoggerFactory.getLogger(AbstractCachedObjectPool.class);
    }
    
    @Override
    public Ref<T> retainObject() {
        final T obj = this._caches.poll();
        if ( null != obj ) {
            final int cachedCount = this._cachedCounter.decrementAndGet();
            final int retainedCount = addToRetained(obj);
            if ( LOG.isTraceEnabled() ) {
                LOG.trace("AbstractCachedObjectPool: retainObject({}) succeed, now cached {}/retained {}", 
                        obj.getClass(), cachedCount, retainedCount);
            }
            return new RefImpl(obj);
        }
        else {
            final T newobj = createObject();
            if ( null == newobj ) {
                LOG.error("AbstractCachedObjectPool: createObject return null, current cached {}/retained {}",
                        this._cachedCounter.get(), this._retainedCounter.get());
                throw new IllegalStateException("AbstractCachedObjectPool: createObject return null");
            }
            final int retainedCount = addToRetained(newobj);
            if ( LOG.isTraceEnabled() ) {
                LOG.trace("AbstractCachedObjectPool: retainObject({}) succeed, now cached {}/retained {}", 
                        newobj.getClass(), this._cachedCounter.get(), retainedCount);
            }
            return new RefImpl(newobj);
        }
    }

    /**
     * @param newobj
     */
    private int addToRetained(final T newobj) {
        if ( this._retains.offer(newobj) ) {
            return this._retainedCounter.incrementAndGet();
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
            return this._retainedCounter.decrementAndGet();
        }
        else {
            return this._retainedCounter.get();
        }
    }
    
    @Override
    public int getCachedCount() {
        return this._cachedCounter.get();
    }
    
    @Override
    public int getRetainedCount() {
        return this._retainedCounter.get();
    }
    
    @Override
    public int getTotalCount() {
        return getCachedCount() + getRetainedCount();
    }
    
    private final class RefImpl 
        extends AbstractReferenceCounted<Ref<T>>
        implements Ref<T> {

        RefImpl(final T obj) {
            this._obj.set(obj);
        }
        
        @Override
        protected void deallocate() {
            AbstractCachedObjectPool.this.returnObject(this._obj.getAndSet(null));
        }

        @Override
        public T object() {
            return this._obj.get();
        }
        
        private final AtomicReference<T> _obj = new AtomicReference<T>(null);
    }
    
    private void returnObject(final T obj) {
        if ( this._caches.offer(obj) ) {
            final int cachedCount = this._cachedCounter.incrementAndGet();
            final int retainedCount = removeFromRetained(obj);
            if ( LOG.isTraceEnabled() ) {
                LOG.trace("AbstractCachedObjectPool: returnObject({}) succeed, now cached {}/retained {}", 
                        obj.getClass(), cachedCount, retainedCount);
            }
        }
    }

    protected abstract T createObject();
    
    protected final Queue<T> _caches = new ConcurrentLinkedQueue<T>();
    protected final Queue<T> _retains = new ConcurrentLinkedQueue<T>();
    protected final AtomicInteger _cachedCounter = new AtomicInteger(0);
    protected final AtomicInteger _retainedCounter = new AtomicInteger(0);
}
