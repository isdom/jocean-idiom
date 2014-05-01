/**
 * 
 */
package org.jocean.idiom.block;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jocean.idiom.ReferenceCounted;
import org.jocean.idiom.pool.ObjectPool.Ref;

/**
 * @author isdom
 *
 */
public class BlocksReadableSupport<T> {
    
    public BlocksReadableSupport(
            final Collection<Ref<T>> blocks, final int length) {
        this._blocks = new ArrayList<Ref<T>>(blocks.size());
        ReferenceCounted.Utils.copyAllAndRetain(blocks, this._blocks);
        this._sizePerBlock = Array.getLength( this._blocks.get(0).object() );
        this._length = length;
    }
    
    public int sizePerBlock() {
        return this._sizePerBlock;
    }
    
    public int getReadPositionInBlockAndIncrement() {
        final int inBlockReadPos = this._inBlockReadIndex++;
        this._globalReadIndex++;
        adjustReadIndex();
        return inBlockReadPos;
    }
    
    public void incrementReadPosition(final int readSize) {
        this._inBlockReadIndex += readSize;
        this._globalReadIndex += readSize;
        adjustReadIndex();
    }
    
    private void adjustReadIndex() {
        if ( this._inBlockReadIndex >= this._sizePerBlock ) {
            this._blockIndex++;
            this._inBlockReadIndex = 0;
        }
    }
    
    public void clear() {
        ReferenceCounted.Utils.releaseAllAndClear(this._blocks);
        this._inBlockReadIndex = 0;
        this._blockIndex = 0;
        this._globalReadIndex = 0;
    }
    
    public int currentPosition() {
        return this._globalReadIndex;
    }
    
    public void adjustReadPositionTo(final int pos) {
        this._globalReadIndex = pos;
        this._blockIndex = pos / this._sizePerBlock;
        this._inBlockReadIndex = pos % this._sizePerBlock;
    }
    
    public T currentBlock() {
        return this._blocks.get( this._blockIndex ).object();
    }

    public int currentReadPositionInBlock() {
        return this._inBlockReadIndex;
    }
    
    public int available() {
        return this._length - this._globalReadIndex;
    }
    
    //  当前的buf中的位置
    private int _inBlockReadIndex = 0;
    
    //  目前在第几个buf
    private int _blockIndex = 0;
    
    //  总位置
    private int _globalReadIndex = 0;
    
    /**
     * An array of bytes that was provided
     * by the creator of the stream. Elements <code>buf[0]</code>
     * through <code>buf[count-1]</code> are the
     * only bytes that can ever be read from the
     * stream;  element <code>buf[pos]</code> is
     * the next byte to be read.
     */
    private final List<Ref<T>> _blocks;
    
    private final int _sizePerBlock;
    private final int _length;
}
