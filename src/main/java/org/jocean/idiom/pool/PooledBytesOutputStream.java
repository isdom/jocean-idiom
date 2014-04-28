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
import org.jocean.idiom.pool.ObjectPool.Ref;


/**
 * @author isdom
 *
 */
public class PooledBytesOutputStream extends OutputStream {

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
            this._writePosition = 0;
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
            final BlobImpl blob = new BlobImpl(this._bytesList, currentBytesLength());
            for ( Ref<byte[]> bytes : this._bytesList ) {
                bytes.release();
            }
            this._bytesList.clear();
            this._writePosition = 0;
            return blob;
        }
        finally {
            this._guard.leave(null);
        }
    }

    /**
     * @return
     */
    private int currentBytesLength() {
        return (this._bytesList.size() - 1) * this._pool.getBlockSize() + this._writePosition;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this._guard.enter(null);
        
        try {
            final byte[] currentBytes = currentBytes();
            currentBytes[this._writePosition++] = (byte)b;
            if ( this._writePosition >= currentBytes.length ) {
                addMoreBytes();
                this._writePosition = 0;
            }
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
        final byte[] bytes = this._bytesList.get( this._bytesList.size() - 1).object();
        return bytes;
    }

    private void addMoreBytes() {
        this._bytesList.add(this._pool.retainObject());
    }

    private int _writePosition = 0;
    
    private final List<Ref<byte[]>> _bytesList = 
            new LinkedList<Ref<byte[]>>();
    
    private final BytesPool _pool;
    
    private final ConcurrentInvokeGuard _guard = new ConcurrentInvokeGuard();
}
