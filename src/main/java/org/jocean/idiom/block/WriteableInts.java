/**
 * 
 */
package org.jocean.idiom.block;

import java.io.Closeable;

/**
 * @author isdom
 *
 */
public interface WriteableInts extends Closeable {
    
    public IntsBlob drainToIntsBlob();
    
    public void write(final int data);
}
