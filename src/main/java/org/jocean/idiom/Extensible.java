/**
 * 
 */
package org.jocean.idiom;

/**
 * @author isdom
 *
 */
public interface Extensible {
    public <EXT> EXT getExtend(final Class<EXT> type);
}
