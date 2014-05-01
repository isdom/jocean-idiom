package org.jocean.idiom.block;

import java.io.Closeable;

public interface ReadableInts extends Closeable {
    
    public int available();
    
    public int read();
}
