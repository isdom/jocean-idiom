package org.jocean.idiom;

import rx.functions.Action0;
import rx.functions.Action1;

public interface Endable {

    public Action1<Action0> onEnd();

    public Action0 doOnEnd(final Action0 onend);
}
