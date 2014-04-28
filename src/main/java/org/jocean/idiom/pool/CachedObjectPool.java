package org.jocean.idiom.pool;

public interface CachedObjectPool<T> extends ObjectPool<T> {
    
    public int getCachedCount();
    
    public int getRetainedCount();

    public int getTotalCount();
    
    public int getTotalCachedSizeInByte();
    
    public int getTotalRetainedSizeInByte();
    
    public int getTotalSizeInByte();
}
