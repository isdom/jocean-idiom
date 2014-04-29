package org.jocean.idiom.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jocean.idiom.AbstractReferenceCounted;
import org.jocean.idiom.ReferenceCounted;
import org.jocean.idiom.pool.ObjectPool.Ref;

final class IntsBlobImpl extends AbstractReferenceCounted<IntsBlob> 
    implements IntsBlob {

    IntsBlobImpl(final Collection<Ref<int[]>> blocks, final int length) {
        this._blocks = new ArrayList<Ref<int[]>>(blocks.size());
        
        ReferenceCounted.Utils.copyAllAndRetain(blocks, this._blocks);
        this._length = length;
    }
    
    @Override
    protected void deallocate() {
        ReferenceCounted.Utils.releaseAllAndClear(this._blocks);
    }
    
    @Override
    public int length() {
        return this._length;
    }

    @Override
    public ReadableInts genReadable() {
        return new ReadableIntsImpl(this._blocks, this._length);
    }

    private final List<Ref<int[]>> _blocks;
    private final int _length;
}
