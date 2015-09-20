/**
 * 
 */
package org.jocean.idiom;

import rx.functions.Action1;

/**
 * @author isdom
 *
 */
public interface Emitter<T> {
    public void emit(final Action1<T> receptor);
}
