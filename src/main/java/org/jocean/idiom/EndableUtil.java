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
public class EndableUtil {

    private EndableUtil() {
        throw new IllegalStateException("No instances!");
    }

    public static Endable nop() {
        return new Endable() {

            @Override
            public Action1<Action0> onEnd() {
                return Actions.empty();
            }

            @Override
            public Action0 doOnEnd(final Action0 onTerminate) {
                return Actions.empty();
            }};
    }
}
