/**
 * 
 */
package org.jocean.idiom.block;

import java.util.ArrayList;
import java.util.List;

import org.jocean.idiom.Pair;
import org.jocean.idiom.ReferenceCounted;
import org.jocean.idiom.pool.BlockPool;
import org.jocean.idiom.pool.ObjectPool.Ref;

/**
 * @author isdom
 *
 */
public class BlocksWriteableSupport<T> {
    
    public BlocksWriteableSupport(final BlockPool<T> pool) {
        this._pool = pool;
    }
    
    @SuppressWarnings("unchecked")
    public <POOL extends BlockPool<T>> POOL pool() {
        return (POOL)this._pool;
    }
    
    public Pair<List<Ref<T>>, Integer> exportBlocks() {
        if ( this._blocks.isEmpty() ) {
            return null;
        }
        else {
            return Pair.of(this._blocks, this._globalWriteIndex);
        }
    }
    
    public T getBlockAt(final int pos) {
        return this._blocks.get( pos / this._pool.getBlockSize() ).object();
    }
    
    public int getWritePositionInBlockAt(final int pos) {
        return pos % this._pool.getBlockSize();
    }
    
    public int getAndIncrementWritePositionInBlock() {
        final int inBlockWritePos = this._inBlockWriteIndex++;
        this._globalWriteIndex++;
        if ( this._inBlockWriteIndex >= this._pool.getBlockSize() ) {
            ensureCapacity(this._globalWriteIndex + 1);
            this._blockIndex++;
            this._inBlockWriteIndex = 0;
        }
        return inBlockWritePos;
    }
    
    public void clear() {
        ReferenceCounted.Utils.releaseAllAndClear(this._blocks);
        this._inBlockWriteIndex = 0;
        this._blockIndex = 0;
        this._globalWriteIndex = 0;
    }
    
    public void adjustWritePositionTo(final int pos) {
        this._globalWriteIndex = pos;
        this._blockIndex = pos / this._pool.getBlockSize();
        this._inBlockWriteIndex = pos % this._pool.getBlockSize();
    }
    
    public T currentBlock() {
        if ( this._blocks.isEmpty() ) {
            addMoreBlock();
        }
        return this._blocks.get( this._blockIndex ).object();
    }

    public void ensureCapacity(int capacity) {
        while ( rawCapacity() < capacity ) {
            addMoreBlock();
        }
    }
    
    private void addMoreBlock() {
        this._blocks.add(this._pool.retainObject());
    }
    
    private int rawCapacity() {
        return this._blocks.size() * this._pool.getBlockSize();
    }
    
    private int _inBlockWriteIndex = 0;
    private int _blockIndex = 0;
    private int _globalWriteIndex = 0;
    
    protected final List<Ref<T>> _blocks = 
            new ArrayList<Ref<T>>();
    
    private final BlockPool<T> _pool;
}
