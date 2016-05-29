package org.jocean.idiom.rx;

import java.util.Collection;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;
import rx.functions.Action4;
import rx.functions.Action5;
import rx.functions.Action6;
import rx.functions.Action7;
import rx.functions.Action8;
import rx.functions.Action9;
import rx.functions.ActionN;

public class RxActions {
    private RxActions() {
        throw new IllegalStateException("No instances!");
    }

    public static <K, V> Action0 doPut(final Map<K,V> map, final K key, final V value) {
        map.put(key, value);
        return new Action0() {
            @Override
            public void call() {
                map.remove(key);
            }};
    }

    public static <V> Action0 doAdd(final Collection<V> collection, final V value) {
        collection.add(value);
        return new Action0() {
            @Override
            public void call() {
                collection.remove(value);
            }};
    }
    
    public static Action0 subscription2Action0(final Subscription subscription) {
        return new Action0() {
            @Override
            public void call() {
                if (!subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }};
    }

    private static final class Action0Adaptor<T0, T1, T2, T3, T4, T5, T6, T7, T8> implements
        Action1<T0>,
        Action2<T0, T1>,
        Action3<T0, T1, T2>,
        Action4<T0, T1, T2, T3>,
        Action5<T0, T1, T2, T3, T4>,
        Action6<T0, T1, T2, T3, T4, T5>,
        Action7<T0, T1, T2, T3, T4, T5, T6>,
        Action8<T0, T1, T2, T3, T4, T5, T6, T7>,
        Action9<T0, T1, T2, T3, T4, T5, T6, T7, T8>,
        ActionN {
        private final Action0 _action0;
        Action0Adaptor(final Action0 action0) {
            this._action0 = action0;
        }
        
        @Override
        public void call(T0 t1) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2, T2 t3) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2, T2 t3, T3 t4) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2, T2 t3, T3 t4, T4 t5) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2, T2 t3, T3 t4, T4 t5, T5 t6) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2, T2 t3, T3 t4, T4 t5, T5 t6, T6 t7) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2, T2 t3, T3 t4, T4 t5, T5 t6, T6 t7, T7 t8) {
            this._action0.call();
        }
        
        @Override
        public void call(T0 t1, T1 t2, T2 t3, T3 t4, T4 t5, T5 t6, T6 t7, T7 t8, T8 t9) {
            this._action0.call();
        }
        
        @Override
        public void call(Object... args) {
            this._action0.call();
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T0, T1, T2, T3, T4, T5, T6, T7, T8> Action0Adaptor<T0, T1, T2, T3, T4, T5, T6, T7, T8> 
        fromAction0(final Action0 action0) {
        return new Action0Adaptor(action0);
    }

    //  apply action to array's each element
    public static <T> void applyArrayBy(final T[] array,
            final Action1<T> actionToElement) {
        for (T element : array) {
            actionToElement.call(element);
        }
    }
    
    public static Action0 toAction0(final ActionN action) {
        return new Action0() {
            @Override
            public void call() {
                action.call();
            }};
    }

    public static <T> Action1<T> toAction1(final ActionN action) {
        return new Action1<T>() {
            @Override
            public void call(final T t) {
                action.call(t);
            }};
    }

    public static <T0, T1> Action2<T0, T1> toAction2(final ActionN action) {
        return new Action2<T0, T1>() {
            @Override
            public void call(final T0 t1, final T1 t2) {
                action.call(t1, t2);
            }};
    }
}
