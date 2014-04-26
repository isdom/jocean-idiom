package org.jocean.idiom;

import java.io.InputStream;


/**
 * @author isdom
 *
 */
public interface Blob extends ReferenceCounted<Blob> {
    
    public int length();
    
    public InputStream genInputStream();
}
