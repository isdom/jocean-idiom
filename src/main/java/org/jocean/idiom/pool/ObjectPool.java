package org.jocean.idiom.pool;

import org.jocean.idiom.ReferenceCounted;


public interface ObjectPool<T> {
    
    public interface Ref<T> extends ReferenceCounted<Ref<T>> {
        public T object();
    }
    
    public Ref<T> retainObject();
}
