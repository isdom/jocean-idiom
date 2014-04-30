/**
 * 
 */
package org.jocean.idiom.block;

/**
 * @author isdom
 *
 */
public interface RandomAccessBytes {
    
    public void writeAt(final int index, final int b);
    
    public int getAt(final int index);
}
