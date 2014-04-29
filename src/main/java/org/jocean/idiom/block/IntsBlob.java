package org.jocean.idiom.block;

import org.jocean.idiom.ReferenceCounted;

public interface IntsBlob extends ReferenceCounted<IntsBlob> {

    public int length();
    
    public ReadableInts genReadable();
}
