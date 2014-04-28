/**
 * 
 */
package org.jocean.idiom.pool;

/**
 * @author isdom
 *
 */
class CachedBytesPool extends AbstractCachedObjectPool<byte[]> 
    implements BytesPool, CachedObjectPool<byte[]> {

    public CachedBytesPool(final int blockSize) {
        if ( blockSize <= 0 ) {
            throw new IllegalArgumentException("blockSize for CachedBytesPool must more than zero.");
        }
        this._blockSize = blockSize;
    }
    
    @Override
    protected byte[] createObject() {
        return new byte[this._blockSize];
    }

    @Override
    public int getTotalCachedSizeInByte() {
        return this.getCachedCount() * this._blockSize;
    }

    @Override
    public int getTotalRetainedSizeInByte() {
        return this.getRetainedCount() * this._blockSize;
    }
    
    @Override
    public int getTotalSizeInByte() {
        return (this.getCachedCount() + this.getRetainedCount() ) * this._blockSize;
    }
    
    @Override
    public int getBlockSize() {
        return this._blockSize;
    }
    
    private final int _blockSize;
}
