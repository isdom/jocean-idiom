package org.jocean.idiom.block;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jocean.idiom.Blob;
import org.jocean.idiom.ExceptionUtils;
import org.jocean.idiom.pool.BytesPool;
import org.jocean.idiom.pool.ObjectPool.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BlockUtils {
    private static final Logger LOG =
            LoggerFactory.getLogger(BlockUtils.class);
    
    public static long inputStream2OutputStream(
            final InputStream is, final PooledBytesOutputStream os) {
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

    public static Blob file2Blob(final File file, final BytesPool pool) {
        InputStream is = null;
        PooledBytesOutputStream os = null;
        
        try {
            is = new FileInputStream(file);
            os = new PooledBytesOutputStream(pool);
            inputStream2OutputStream(is, os);
            return os.drainToBlob();
        }
        catch(Exception e) {
            LOG.warn("exception when file2Blob for file {}, detail: {}", 
                    file, ExceptionUtils.exception2detail(e) );
        }
        finally {
            if ( null != is ) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if ( null != os ) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
