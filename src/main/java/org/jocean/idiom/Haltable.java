package org.jocean.idiom;

import rx.functions.Action0;
import rx.functions.Action1;

public interface Haltable {

    public Action1<Action0> onHalt();

    public Action0 doOnHalt(final Action0 onhalt);
}
