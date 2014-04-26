/**
 * 
 */
package org.jocean.idiom.pool;

/**
 * @author isdom
 *
 */
public class ByteArrayPool extends AbstractObjectPool<byte[]> {

    public ByteArrayPool(final int blockSize) {
        if ( blockSize <= 0 ) {
            throw new IllegalArgumentException("blockSize for ByteArrayPool must more than zero.");
        }
        this._blockSize = blockSize;
    }
    
    @Override
    protected byte[] createObject() {
        return new byte[this._blockSize];
    }

    public int getTotalSizeInByte() {
        return this.getIdleCount() * _blockSize;
    }
    
    public int getBlockSize() {
        return this._blockSize;
    }
    
    private final int _blockSize;
}
