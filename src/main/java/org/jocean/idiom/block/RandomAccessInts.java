/**
 * 
 */
package org.jocean.idiom.block;

/**
 * @author isdom
 *
 */
public interface RandomAccessInts {
    
    public void writeAt(final int index, final int data);
    
    public int getAt(final int index);
}
