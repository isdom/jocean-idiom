package org.jocean.idiom.block;

import org.jocean.idiom.ReferenceCounted;

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
    
    public int length();
    
    public ReadableInts genReadable();
}
