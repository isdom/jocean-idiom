package org.jocean.idiom;

import rx.functions.Action0;
import rx.functions.Action1;

public interface TerminateAware<T> {
    
    public Action1<Action0> onTerminate();
    
    public Action0 doOnTerminate(final Action1<T> onTerminate);
}
