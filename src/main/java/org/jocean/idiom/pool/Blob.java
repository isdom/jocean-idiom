package org.jocean.idiom.pool;

import java.io.InputStream;

import org.jocean.idiom.ReferenceCounted;

/**
 * @author isdom
 *
 */
public interface Blob extends ReferenceCounted<Blob> {
    
    public int length();
    
    public InputStream genInputStream();
}
