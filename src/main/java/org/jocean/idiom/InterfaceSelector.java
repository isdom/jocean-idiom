package org.jocean.idiom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import rx.functions.ActionN;

public class InterfaceSelector {
    
    public synchronized <T> T build(final Class<T> cls, final T actived, final T unactived) {
        if (null != this._active) {
            this._active = Arrays.copyOf(this._active, this._active.length + 1);
            this._unactived = Arrays.copyOf(this._unactived, this._unactived.length + 1);
        } else if (null == this._unactived) {
            this._active = new Object[] {actived};
            this._unactived = new Object[] {unactived};
        } else {
            throw new RuntimeException("selector has destroy.");
        }
        final int idx = this._active.length - 1;
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        @SuppressWarnings("unchecked")
        final T proxy = (T) Proxy.newProxyInstance(
            cl, new Class<?>[]{cls}, new InvocationHandler() {
                public Object invoke(final Object obj, final Method method, final Object[] args)
                        throws Throwable {
                    return invokeWithIdx(idx, method, args);
                }
            });
        return proxy;
    }
    
    public Object invokeWithIdx(final int idx, final Method method, final Object[] args)
            throws Throwable {
        synchronized(this) {
            final Object[] impl = activeUpdater.get(this);
            if (null != impl) {
                return method.invoke(impl[idx], args);
            }
        }
        final Object target = this._unactived[idx];
        return null != target ? method.invoke(target, args) : null;
    }
    
    public boolean isActive() {
        return null != activeUpdater.get(this);
    }
    
    public void destroy(final ActionN actionWhenDestroying, final Object... args) {
        Object[] actived;
        synchronized(this) {
            actived = activeUpdater.getAndSet(this, null);
        }
        if (null!=actived && null!=actionWhenDestroying) {
            actionWhenDestroying.call(args);
        }
    }

    private static final AtomicReferenceFieldUpdater<InterfaceSelector, Object[]> activeUpdater =
            AtomicReferenceFieldUpdater.newUpdater(InterfaceSelector.class, Object[].class, "_active");

    private volatile Object[] _active = null;
    private Object[] _unactived = null;
}
