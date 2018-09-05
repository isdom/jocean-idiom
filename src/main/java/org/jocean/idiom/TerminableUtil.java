/**
 *
 */
package org.jocean.idiom;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * @author isdom
 *
 */
public class TerminableUtil {

    private TerminableUtil() {
        throw new IllegalStateException("No instances!");
    }

    public static Terminable nop() {
        return new Terminable() {

            @Override
            public Action1<Action0> onTerminate() {
                return Actions.empty();
            }

            @Override
            public Action0 doOnTerminate(final Action0 onTerminate) {
                return Actions.empty();
            }};
    }
}
