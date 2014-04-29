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
        this._sizePerBlock = this._blocks.get(0).object().length;
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
    
    @Override
    public int sizePerBlock() {
        return this._sizePerBlock;
    }

    @Override
    public int totalBlockCount() {
        return this._blocks.size();
    }

    @Override
    public int[] getBlockAt(int idx) {
        return this._blocks.get(idx).object();
    }

    @Override
    public void writeAt(int index, int data) {
        getBlockAt(index / this._sizePerBlock)[index % this._sizePerBlock] = data;
    }

    @Override
    public int getAt(int index) {
        return getBlockAt(index / this._sizePerBlock)[index % this._sizePerBlock];
    }
    
    private final List<Ref<int[]>> _blocks;
    private final int _length;
    private final int _sizePerBlock;
}
