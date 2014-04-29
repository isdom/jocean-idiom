/**
 * 
 */
package org.jocean.idiom.block;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.jocean.idiom.Blob;
import org.jocean.idiom.ConcurrentInvokeGuard;
import org.jocean.idiom.Pair;
import org.jocean.idiom.pool.BytesPool;
import org.jocean.idiom.pool.ObjectPool.Ref;


/**
 * @author isdom
 *
 */
public class PooledBytesOutputStream extends OutputStream 
    implements RandomAccessBytes {

    public PooledBytesOutputStream(final BytesPool pool) {
        this._support = new BlocksWriteableSupport<byte[]>(pool);
    }

    public BytesPool pool() {
        return this._support.pool();
    }
    
    public void clear() {
        this._guard.enter(null);
        
        try {
            this._support.clear();
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    public Blob drainToBlob() {
        this._guard.enter(null);
        
        try {
            final Pair<List<Ref<byte[]>>,Integer> blocks = this._support.exportBlocks();
            if ( null == blocks) {
                return null;
            }
            final BlobImpl blob = new BlobImpl(blocks.getFirst(), blocks.getSecond());
            this._support.clear();
            return blob;
        }
        finally {
            this._guard.leave(null);
        }
    }

    @Override
    public void write(final int b) throws IOException {
        this._guard.enter(null);
        
        try {
            final byte[] currentBytes = this._support.currentBlock();
            currentBytes[this._support.getAndIncrementWritePositionInBlock()] = (byte)b;
        }
        finally {
            this._guard.leave(null);
        }
    }

    @Override
    public void setCapacity(final int capacity) {
        this._guard.enter(null);
        
        try {
            this._restrictionCapacity = capacity;
            this._support.ensureCapacity(capacity);
            this._support.adjustWritePositionTo(capacity);
        }
        finally {
            this._guard.leave(null);
        }
    }

    @Override
    public int getCapacity() {
        return this._restrictionCapacity;
    }
    
    @Override
    public void writeAt(final int index, final int b) {
        if ( index >= this._restrictionCapacity) {
            throw new IllegalArgumentException("writeAt's index(" + index +  ") exceed capacity:" + this._restrictionCapacity);
        }
        
        this._guard.enter(null);
        
        try {
            this._support.getBlockAt(index)[
                this._support.getWritePositionInBlockAt(index)] = (byte)b;
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    @Override
    public void close() throws IOException {
        clear();
    }
    
    private final BlocksWriteableSupport<byte[]> _support;
    
    private volatile int _restrictionCapacity = 0;
    
    private final ConcurrentInvokeGuard _guard = new ConcurrentInvokeGuard();
}
