package org.jocean.idiom;

import rx.functions.Action0;
import rx.functions.Action1;

public interface Terminable {
    
    public Action1<Action0> onTerminate();
    
    public Action0 doOnTerminate(final Action0 onTerminate);
}
