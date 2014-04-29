/**
 * 
 */
package org.jocean.idiom.block;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.jocean.idiom.ConcurrentInvokeGuard;
import org.jocean.idiom.pool.ObjectPool.Ref;

/**
 * @author isdom
 *
 */
final class ReferenceCountedBytesListInputStream extends InputStream {

    /**
     * Creates a <code>ByteArrayInputStream</code>
     * so that it  uses <code>buf</code> as its
     * buffer array. 
     * The buffer array is not copied. 
     * The initial value of <code>pos</code>
     * is <code>0</code> and the initial value
     * of  <code>count</code> is the length of
     * <code>buf</code>.
     *
     * @param   buf   the input buffer.
     */
    ReferenceCountedBytesListInputStream(
            final Collection<Ref<byte[]>> blocks, final int length) {
        this._support = new BlocksReadableSupport<byte[]>(blocks, length);
    }

    /**
     * Reads the next byte of data from this input stream. The value 
     * byte is returned as an <code>int</code> in the range 
     * <code>0</code> to <code>255</code>. If no byte is available 
     * because the end of the stream has been reached, the value 
     * <code>-1</code> is returned. 
     * <p>
     * This <code>read</code> method 
     * cannot block. 
     *
     * @return  the next byte of data, or <code>-1</code> if the end of the
     *          stream has been reached.
     */
    public int read() {
    	
        this._guard.enter(null);
        
        try {
            if ( this._support.available() > 0 ) {
                final byte[] block = this._support.currentBlock();
                return (block[this._support.getAndIncrementReadPositionInBlock()] & 0xff);
            }
            else {
                return -1;
            }
        }
        finally {
            this._guard.leave(null);
        }
    }

    /**
     * Reads up to <code>len</code> bytes of data into an array of bytes 
     * from this input stream. 
     * If <code>pos</code> equals <code>count</code>,
     * then <code>-1</code> is returned to indicate
     * end of file. Otherwise, the  number <code>k</code>
     * of bytes read is equal to the smaller of
     * <code>len</code> and <code>count-pos</code>.
     * If <code>k</code> is positive, then bytes
     * <code>buf[pos]</code> through <code>buf[pos+k-1]</code>
     * are copied into <code>b[off]</code>  through
     * <code>b[off+k-1]</code> in the manner performed
     * by <code>System.arraycopy</code>. The
     * value <code>k</code> is added into <code>pos</code>
     * and <code>k</code> is returned.
     * <p>
     * This <code>read</code> method cannot block. 
     *
     * @param   b     the buffer into which the data is read.
     * @param   off   the start offset in the destination array <code>b</code>
     * @param   len   the maximum number of bytes read.
     * @return  the total number of bytes read into the buffer, or
     *          <code>-1</code> if there is no more data because the end of
     *          the stream has been reached.
     * @exception  NullPointerException If <code>b</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>off</code> is negative, 
     * <code>len</code> is negative, or <code>len</code> is greater than 
     * <code>b.length - off</code>
     */
    //	TODO, impl later
//    public synchronized int read(byte b[], int off, int len) {
//	if (b == null) {
//	    throw new NullPointerException();
//	} else if (off < 0 || len < 0 || len > b.length - off) {
//	    throw new IndexOutOfBoundsException();
//	}
//	if (pos >= count) {
//	    return -1;
//	}
//	if (pos + len > count) {
//	    len = count - pos;
//	}
//	if (len <= 0) {
//	    return 0;
//	}
//	System.arraycopy(buf, pos, b, off, len);
//	pos += len;
//	return len;
//    }

    /**
     * Skips <code>n</code> bytes of input from this input stream. Fewer 
     * bytes might be skipped if the end of the input stream is reached. 
     * The actual number <code>k</code>
     * of bytes to be skipped is equal to the smaller
     * of <code>n</code> and  <code>count-pos</code>.
     * The value <code>k</code> is added into <code>pos</code>
     * and <code>k</code> is returned.
     *
     * @param   n   the number of bytes to be skipped.
     * @return  the actual number of bytes skipped.
     */
    //	TODO, impl later
//    public synchronized long skip(long n) {
//	if (pos + n > count) {
//	    n = count - pos;
//	}
//	if (n < 0) {
//	    return 0;
//	}
//	pos += n;
//	return n;
//    }

    /**
     * Returns the number of remaining bytes that can be read (or skipped over)
     * from this input stream.
     * <p>
     * The value returned is <code>count&nbsp;- pos</code>, 
     * which is the number of bytes remaining to be read from the input buffer.
     *
     * @return  the number of remaining bytes that can be read (or skipped
     *          over) from this input stream without blocking.
     */
    public int available() {
        this._guard.enter(null);
        
        try {
            return this._support.available();
        }
        finally {
            this._guard.leave(null);
        }
    }

    /**
     * Tests if this <code>InputStream</code> supports mark/reset. The
     * <code>markSupported</code> method of <code>ByteArrayInputStream</code>
     * always returns <code>true</code>.
     *
     * @since   JDK1.1
     */
    public boolean markSupported() {
    	return true;
    }

    /**
     * Set the current marked position in the stream.
     * ByteArrayInputStream objects are marked at position zero by
     * default when constructed.  They may be marked at another
     * position within the buffer by this method.
     * <p>
     * If no mark has been set, then the value of the mark is the
     * offset passed to the constructor (or 0 if the offset was not
     * supplied).
     *
     * <p> Note: The <code>readAheadLimit</code> for this class
     *  has no meaning.
     *
     * @since   JDK1.1
     */
    public void mark(int readAheadLimit) {
        this._guard.enter(null);
        
        try {
            this.markedPosition = this._support.currentPosition();
        }
        finally {
            this._guard.leave(null);
        }
    }

    /**
     * Resets the buffer to the marked position.  The marked position
     * is 0 unless another position was marked or an offset was specified
     * in the constructor.
     */
    public void reset() {
        this._guard.enter(null);
        
        try {
            this._support.adjustReadPositionTo( this.markedPosition);
        }
        finally {
            this._guard.leave(null);
        }
    }

    /**
     * Closing a <tt>ByteArrayInputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     * <p>
     */
    public void close() throws IOException {
        this._guard.enter(null);
        
        try {
            this._support.clear();
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    private final BlocksReadableSupport<byte[]> _support;
    private int markedPosition = 0;
    
    private final ConcurrentInvokeGuard _guard = 
            new ConcurrentInvokeGuard();
}
