/**
 * 
 */
package org.jocean.idiom.pool;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.jocean.idiom.Blob;
import org.jocean.idiom.ConcurrentInvokeGuard;
import org.jocean.idiom.RandomAccessBytes;
import org.jocean.idiom.pool.ObjectPool.Ref;


/**
 * @author isdom
 *
 */
public class PooledBytesOutputStream extends OutputStream 
    implements RandomAccessBytes {

    public PooledBytesOutputStream(final BytesPool pool) {
        this._pool = pool;
    }

    public BytesPool pool() {
        return this._pool;
    }
    
    public void clear() {
        this._guard.enter(null);
        
        try {
            for ( Ref<byte[]> bytes : this._bytesList ) {
                bytes.release();
            }
            this._bytesList.clear();
            this._inBlockWriteIndex = 0;
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    public Blob drainToBlob() {
        
        this._guard.enter(null);
        
        try {
            if ( this._bytesList.isEmpty() ) {
                return null;
            }
            final BlobImpl blob = new BlobImpl(this._bytesList, this._writeIndex);
            for ( Ref<byte[]> bytes : this._bytesList ) {
                bytes.release();
            }
            this._bytesList.clear();
            this._inBlockWriteIndex = 0;
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
            final byte[] currentBytes = currentBytes();
            currentBytes[this._inBlockWriteIndex++] = (byte)b;
            this._writeIndex++;
            if ( this._inBlockWriteIndex >= currentBytes.length ) {
                ensureCapacity(this._writeIndex);
                this._blockIndex++;
                this._inBlockWriteIndex = 0;
            }
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
            ensureCapacity(capacity);
            adjustWritePositionTo(capacity);
        }
        finally {
            this._guard.leave(null);
        }
    }

    /**
     * @param pos
     */
    private void adjustWritePositionTo(final int pos) {
        this._writeIndex = pos;
        this._blockIndex = pos / this._pool.getBlockSize();
        this._inBlockWriteIndex = pos % this._pool.getBlockSize();
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
            ensureCapacity(index+1);
            final int blockidx = index / this._pool.getBlockSize();
            final int inBlockIdx = index % this._pool.getBlockSize();
            this._bytesList.get( blockidx ).object()[inBlockIdx] = (byte)b;
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    @Override
    public void close() throws IOException {
        clear();
    }
    
    private byte[] currentBytes() {
        if ( this._bytesList.isEmpty() ) {
            addMoreBytes();
        }
        return this._bytesList.get( this._blockIndex ).object();
    }

    private void addMoreBytes() {
        this._bytesList.add(this._pool.retainObject());
    }
    
    private void ensureCapacity(int capacity) {
        while ( capacity() < capacity ) {
            addMoreBytes();
        }
    }
    
    private int capacity() {
        return this._bytesList.size() * this._pool.getBlockSize();
    }

    private int _inBlockWriteIndex = 0;
    private int _blockIndex = 0;
    private int _writeIndex = 0;
    
    private volatile int _restrictionCapacity = 0;
    
    private final List<Ref<byte[]>> _bytesList = 
            new LinkedList<Ref<byte[]>>();
    
    private final BytesPool _pool;
    
    private final ConcurrentInvokeGuard _guard = new ConcurrentInvokeGuard();
}
