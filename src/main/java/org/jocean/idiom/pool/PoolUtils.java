/**
 * 
 */
package org.jocean.idiom.pool;

import java.io.InputStream;

import org.jocean.idiom.ExceptionUtils;
import org.jocean.idiom.pool.ObjectPool.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author isdom
 *
 */
public class PoolUtils {
    private static final Logger LOG =
            LoggerFactory.getLogger(PoolUtils.class);
    
    public static long inputStream2OutputStream(final InputStream is, final PooledBytesOutputStream os) {
        long totalBytes = 0;
        final Ref<byte[]> bytes = os.pool().retainObject();
        try {
            while ( is.available() > 0 ) {
                final int actualSize = is.read(bytes.object());
                if ( actualSize == -1 ) {
                    break;
                }
                os.write(bytes.object(), 0, actualSize);
                totalBytes += actualSize;
                if ( LOG.isTraceEnabled() ) {
                    LOG.trace("read bytebuf's content to bytesList, size {}", actualSize);
                }
            }
        }
        catch(Exception e) {
            LOG.warn("exception when inputStream -> OutputStream, detail: {}", 
                    ExceptionUtils.exception2detail(e) );
        }
        finally {
            bytes.release();
        }
        return totalBytes;
    }

}
