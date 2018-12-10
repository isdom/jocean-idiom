package org.jocean.idiom;

import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Func1;

public class StepableUtil {

    /*
    final static private Transformer<Stepable<Observable<Object>>, Object> AUTOSTEP_TO_ELEMENT =
        new Transformer<Stepable<Observable<Object>>, Object>() {
            @Override
            public Observable<Object> call(final Observable<Stepable<Observable<Object>>> org) {
                return org.flatMap(new Func1<Stepable<Observable<Object>>, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(final Stepable<Observable<Object>> slice) {
                        try {
                            return slice.element();
                        } finally {
                            slice.step();
                        }
                    }});
            }};
    */

    private StepableUtil() {
        throw new IllegalStateException("No instances!");
    }

    public static <S extends Stepable<Observable<? extends E>>, E> Transformer<S, E> autostep2element() {
//      return (Transformer<S, E>)AUTOSTEP_TO_ELEMENT;
        final Func1<S, Observable<? extends E>> s2e = new Func1<S, Observable<? extends E>>() {
            @Override
            public Observable<? extends E> call(final S slice) {
                try {
                    return slice.element();
                } finally {
                    slice.step();
                }
            }
        };
        return new Transformer<S, E>() {
            @Override
            public Observable<E> call(final Observable<S> org) {
                return org.flatMap(s2e);
            }
        };
    }

    public static <S extends Stepable<Iterable<? extends E>>, E> Transformer<S, E> autostep2element2() {
//      return (Transformer<S, E>)AUTOSTEP_TO_ELEMENT;
        final Func1<S, Observable<? extends E>> s2e = new Func1<S, Observable<? extends E>>() {
            @Override
            public Observable<? extends E> call(final S slice) {
                try {
                    return Observable.from(slice.element());
                } finally {
                    slice.step();
                }
            }
        };
        return new Transformer<S, E>() {
            @Override
            public Observable<E> call(final Observable<S> org) {
                return org.flatMap(s2e);
            }
        };
    }
}
