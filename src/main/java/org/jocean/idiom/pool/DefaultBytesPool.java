package org.jocean.idiom.pool;

class DefaultBytesPool extends AbstractObjectPool<byte[]> 
    implements BytesPool {

    public DefaultBytesPool(final int blockSize) {
        if ( blockSize <= 0 ) {
            throw new IllegalArgumentException("blockSize for DefaultBytesPool must more than zero.");
        }
        this._blockSize = blockSize;
    }
    
    @Override
    public int getBlockSize() {
        return this._blockSize;
    }

    @Override
    protected byte[] createObject() {
        return new byte[this._blockSize];
    }

    private final int _blockSize;
}
