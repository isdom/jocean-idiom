/**
 * 
 */
package org.jocean.idiom.pool;

/**
 * @author isdom
 *
 */
public class CachedIntsPool extends AbstractCachedObjectPool<int[]> 
    implements IntsPool, CachedObjectPool<int[]> {

    public CachedIntsPool(final int blockSize) {
        if ( blockSize <= 0 ) {
            throw new IllegalArgumentException("blockSize for CachedIntsPool must more than zero.");
        }
        this._blockSize = blockSize;
    }
    
    @Override
    protected int[] createObject() {
        return new int[this._blockSize];
    }

    @Override
    public int getTotalCachedSizeInByte() {
        return this.getCachedCount() * this._blockSize * 4;
    }

    @Override
    public int getTotalRetainedSizeInByte() {
        return this.getRetainedCount() * this._blockSize * 4;
    }
    
    @Override
    public int getTotalSizeInByte() {
        return (this.getCachedCount() + this.getRetainedCount() ) * this._blockSize * 4;
    }
    
    @Override
    public int getBlockSize() {
        return this._blockSize;
    }
    
    private final int _blockSize;
}
