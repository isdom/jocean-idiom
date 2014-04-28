package org.jocean.idiom;

import java.io.InputStream;


/**
 * @author isdom
 *
 */
public interface Blob extends ReferenceCounted<Blob> {
    
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
    
    public int length();
    
    public InputStream genInputStream();
}
