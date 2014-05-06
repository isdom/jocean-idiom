/**
 * 
 */
package org.jocean.idiom.pool;


/**
 * @author isdom
 *
 */
public abstract class Pools {
    public static BytesPool createCachedBytesPool(final int blockSize) {
        return new CachedBytesPool(blockSize);
    }
    
    public static BytesPool createDefaultBytesPool(final int blockSize) {
        return new DefaultBytesPool(blockSize);
    }

    public static IntsPool createCachedIntsPool(final int blockSize) {
        return new CachedIntsPool(blockSize);
    }
}
