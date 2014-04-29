package org.jocean.idiom.block;

import java.util.Collection;

import org.jocean.idiom.ConcurrentInvokeGuard;
import org.jocean.idiom.pool.ObjectPool.Ref;

final class ReadableIntsImpl implements ReadableInts {
    
    ReadableIntsImpl(final Collection<Ref<int[]>> blocks, final int length) {
        this._support = new BlocksReadableSupport<int[]>(blocks, length);
    }
    
    public int read() {
        this._guard.enter(null);
        
        try {
            if ( this._support.available() > 0 ) {
                final int[] block = this._support.currentBlock();
                return block[this._support.getAndIncrementReadPositionInBlock()];
            }
            else {
                throw new IllegalStateException("no more data to read.");
//                return -1;
            }
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    public int available() {
        this._guard.enter(null);
        
        try {
            return this._support.available();
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    public void close() {
        this._guard.enter(null);
        
        try {
            this._support.clear();
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    private final BlocksReadableSupport<int[]> _support;
    
    private final ConcurrentInvokeGuard _guard = 
            new ConcurrentInvokeGuard();
    
}
