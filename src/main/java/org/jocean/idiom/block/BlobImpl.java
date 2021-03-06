/**
 * 
 */
package org.jocean.idiom.block;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jocean.idiom.AbstractReferenceCounted;
import org.jocean.idiom.ReferenceCounted;
import org.jocean.idiom.pool.BytesPool;
import org.jocean.idiom.pool.ObjectPool.Ref;

/**
 * @author isdom
 *
 */
final class BlobImpl extends AbstractReferenceCounted<Blob> implements Blob {

    BlobImpl(final Collection<Ref<byte[]>> blocks, 
            final int length, final BytesPool pool) {
        // TODO, test if bytesCollection 's total size >= length
        //  and furthermore we can release unused bytes
        this._blocks = new ArrayList<Ref<byte[]>>(blocks.size());
        
        ReferenceCounted.Utils.copyAllAndRetain(blocks, this._blocks);
        
        this._length = length;
        this._pool = pool;
    }
    
    @Override
    public BytesPool pool() {
        return this._pool;
    }
    
    @Override
    public int length() {
        return this._length;
    }

    @Override
    public InputStream genInputStream() {
        return new ReferenceCountedBytesListInputStream(this._blocks, this._length);
    }

    @Override
    protected void deallocate() {
        ReferenceCounted.Utils.releaseAllAndClear(this._blocks);
    }
    
    @Override
    public void close() throws IOException {
        this.release();
    }
    
    private final List<Ref<byte[]>> _blocks;
    private final int _length;
    private final BytesPool _pool;
}
