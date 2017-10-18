package org.jocean.idiom.rx;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.jocean.idiom.ExceptionUtils;
import org.jocean.idiom.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import rx.functions.Func1;

public class RxActions {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(RxActions.class);
    
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
    
    public static <V> Action0 doCall(final Func1<V, Action0> func, final V value) {
        return func.call(value);
    }
    
    public static <V> Action0 doAddAndNotify(final Collection<V> collection, final V value,final Action0 notifyme) {
        collection.add(value);
        try {
            if (null != notifyme) {
                notifyme.call();
            }
        } catch (Exception e) {
            LOG.warn("exception when invoke notify{}, detail: {}",
                    notifyme, ExceptionUtils.exception2detail(e));
        }
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

    public static <T> Action1<T> toAction1(final Action0 action0) {
        return new Action1<T>() {
            @Override
            public void call(final T t) {
                action0.call();
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

    public static <T> Action1_N<T> toAction1_N(final Class<T> cls, final String methodName) {
        final Method method = ReflectUtils.getMethodNamed(cls, methodName);
        if (null!=method) {
            return new Action1_N<T>() {
                @Override
                public void call(final T obj, final Object... args) {
                    try {
                        method.invoke(obj, args);
                    } catch (Exception e) {
                        LOG.warn("exception when invoke {}.{}, detail: {}",
                                cls, method, ExceptionUtils.exception2detail(e));
                    }
                }};
        } else {
            throw new RuntimeException("invalid method " + methodName +" for class " + cls);
        }
    }

    public static <T0, T1> Action0 bindParameter(final Action2<T0, T1> action2, final T0 t0, final T1 t1) {
        return new Action0() {
            @Override
            public void call() {
                action2.call(t0, t1);
            }};
    }
    
    public static <T0, T1> Action1<T1> bindParameter(final Action2<T0, T1> action2, final T0 t0) {
        return new Action1<T1>() {
            @Override
            public void call(final T1 t1) {
                action2.call(t0, t1);
            }};
    }

    public static <T0, T1> Action1<T0> bindLastParameter(final Action2<T0, T1> action2, final T1 t1) {
        return new Action1<T0>() {
            @Override
            public void call(final T0 t0) {
                action2.call(t0, t1);
            }};
    }
    
    public static <T0> Action0 bindParameter(final Action1<T0> action1, final T0 t0) {
        return new Action0() {
            @Override
            public void call() {
                action1.call(t0);
            }};
    }

    public static <T1, T2> Action1<T2> map(final Action1<T1> action1, final Func1<T2, T1> func) {
        return new Action1<T2>() {
            @Override
            public void call(T2 t2) {
                action1.call(func.call(t2));
            }};
    }
}
