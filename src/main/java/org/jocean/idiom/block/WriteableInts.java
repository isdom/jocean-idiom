/**
 * 
 */
package org.jocean.idiom.block;

import org.jocean.idiom.pool.IntsPool;



/**
 * @author isdom
 *
 */
public interface WriteableInts {
    
    public IntsBlob drainToIntsBlob();
    
    public void write(final int data);
    
    public void close();
    
    public static class Utils {
        public static WriteableInts createWriteableInts(final IntsPool pool) {
            return new WriteableIntsImpl(pool);
        }
    }
}
