package org.jocean.idiom;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

public interface Haltable {

    public Action1<Action0> onHalt();

    public Action0 doOnHalt(final Action0 onhalt);

    public final static Haltable NEVER = new Haltable() {
        @Override
        public Action1<Action0> onHalt() {
            return Actions.empty();
        }
        @Override
        public Action0 doOnHalt(final Action0 onhalt) {
            return Actions.empty();
    }};
}
