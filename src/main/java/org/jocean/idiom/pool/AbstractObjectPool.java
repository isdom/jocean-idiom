package org.jocean.idiom.pool;

import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.AbstractReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(AbstractObjectPool.class);

    @Override
    public Ref<T> retainObject() {
        final T newobj = createObject();
        if ( null == newobj ) {
            LOG.trace("AbstractObjectPool: createObject return null");
            throw new IllegalStateException("AbstractObjectPool: createObject return null");
        }
        return new RefImpl(newobj);
    }

    private final class RefImpl 
        extends AbstractReferenceCounted<Ref<T>>
        implements Ref<T> {

        RefImpl(final T obj) {
            this._obj.set(obj);
        }
        
        @Override
        protected void deallocate() {
            this._obj.set(null);
        }

        @Override
        public T object() {
            return this._obj.get();
        }
        
        private final AtomicReference<T> _obj = new AtomicReference<T>(null);
    }
    
    protected abstract T createObject();
}
