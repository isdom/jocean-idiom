/**
 * 
 */
package org.jocean.idiom.pool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jocean.idiom.AbstractReferenceCounted;
import org.jocean.idiom.pool.ObjectPool.Ref;

/**
 * @author isdom
 *
 */
final class BlobImpl extends AbstractReferenceCounted<Blob> implements Blob {

    BlobImpl(final Collection<Ref<byte[]>> bytesCollecion, 
            final int length) {
        this._bytesList = new ArrayList<Ref<byte[]>>(bytesCollecion.size());
        for ( Ref<byte[]> bytes : bytesCollecion ) {
            this._bytesList.add(bytes.retain());
        }
        
        this._length = length;
    }
    
    @Override
    public int length() {
        return this._length;
    }

    @Override
    public InputStream genInputStream() {
        return new ReferenceCountedBytesListInputStream(this._bytesList, this._length);
    }

    @Override
    protected void deallocate() {
        for ( Ref<byte[]> bytes : this._bytesList ) {
            bytes.release();
        }
        this._bytesList.clear();
    }
    
    private final List<Ref<byte[]>> _bytesList;
    private final int _length;
}
