package org.jocean.idiom.rx;

import rx.Observable;
import rx.Observable.Operator;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.functions.Func0;

public class RxObservables {
    private RxObservables() {
        throw new IllegalStateException("No instances!");
    }

    protected static enum OperatorIgnoreCompleted implements Operator<Object, Object> {
        IGNORE_COMPLETED;

        @Override
        public Subscriber<Object> call(final Subscriber<Object> child) {
            return new Subscriber<Object>(child) {
                @Override
                public void onCompleted() {
                    // Do Nothing
                }
                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }
                @Override
                public void onNext(Object t) {
                    child.onNext(t);
                }
            };
        }
    }
    
    private static final Transformer<Object, Object> IGNORE_COMPLETED_TRANSFORMER = 
        new Transformer<Object, Object>() {
            @Override
            public Observable<Object> call(final Observable<Object> actual) {
                return actual.lift(OperatorIgnoreCompleted.IGNORE_COMPLETED);
            }};
    
    @SuppressWarnings("unchecked")
    public static final <T> Transformer<T, T> ignoreCompleted() {
        return (Transformer<T, T>)IGNORE_COMPLETED_TRANSFORMER;
    }
    
    public static final <T, U> Observable<T> delaySubscriptionUntilCompleted(
            final Observable<T> source, final Observable<U> selector) {
        return source.delaySubscription(new Func0<Observable<U>>() {
            @Override
            public Observable<U> call() {
                return selector.last();
            }});
    }
}
