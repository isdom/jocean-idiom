package org.jocean.idiom;

import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Func1;

public class StepableUtil {

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

        private StepableUtil() {
            throw new IllegalStateException("No instances!");
        }

        @SuppressWarnings("unchecked")
        public static <S extends Stepable<Observable<? extends E>>, E> Transformer<S, E> autostep2element() {
            return (Transformer<S, E>)AUTOSTEP_TO_ELEMENT;
        }
}
