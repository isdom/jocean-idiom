package org.jocean.idiom.pool;

public interface BlockPool<T> extends ObjectPool<T> {
    public int getBlockSize();
}
