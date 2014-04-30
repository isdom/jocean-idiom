package org.jocean.idiom.block;

import org.jocean.idiom.ReferenceCounted;
import org.jocean.idiom.pool.IntsPool;

public interface IntsBlob extends ReferenceCounted<IntsBlob>, RandomAccessBlocks<int[]>, RandomAccessInts {

    public static class Utils {
        public static ReadableInts releaseAndGenReadable(final IntsBlob blob) {
            ReadableInts ints = null;
                    
            if ( null != blob) {
                try {
                    ints = blob.genReadable();
                }
                finally {
                    blob.release();
                }
            }
            return ints;
        }
    }
    
    public IntsPool pool();
    
    public int length();
    
    public ReadableInts genReadable();
}
