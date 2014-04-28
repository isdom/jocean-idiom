package org.jocean.idiom.pool;

public interface BytesPool extends ObjectPool<byte[]> {
    
    public int getBlockSize();
}
