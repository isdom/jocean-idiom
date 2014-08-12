package org.jocean.idiom.block;

import java.io.Closeable;
import java.io.InputStream;

import org.jocean.idiom.ReferenceCounted;
import org.jocean.idiom.pool.BytesPool;


/**
 * @author isdom
 *
 */
public interface Blob extends ReferenceCounted<Blob>, Closeable {
    
    public static class Utils {
        public static InputStream releaseAndGenInputStream(final Blob blob) {
            InputStream is = null;
                    
            if ( null != blob) {
                try {
                    is = blob.genInputStream();
                }
                finally {
                    blob.release();
                }
            }
            return is;
        }
    }
    
    public BytesPool pool();
    
    public int length();
    
    public InputStream genInputStream();
}
