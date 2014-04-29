package org.jocean.idiom.block;


public interface RandomAccessBlocks<BLOCK> {
    
    public int sizePerBlock();
    
    public int totalBlockCount();
    
    public BLOCK getBlockAt(final int idx);
}
