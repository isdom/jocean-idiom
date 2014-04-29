/**
 * 
 */
package org.jocean.idiom.block;




/**
 * @author isdom
 *
 */
public interface WriteableInts {
    
    public IntsBlob drainToIntsBlob();
    
    public void write(final int data);
    
    public void close();
}
