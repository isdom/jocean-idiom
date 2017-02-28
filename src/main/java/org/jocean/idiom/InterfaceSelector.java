package org.jocean.idiom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action0;
import rx.functions.ActionN;

public class InterfaceSelector {
    private static final Logger LOG =
            LoggerFactory.getLogger(InterfaceSelector.class);
    
    public synchronized <T> T build(final Class<T> cls, final T actived, final T unactived) {
        if (null != this._active) {
            this._active = Arrays.copyOf(this._active, this._active.length + 1);
            this._unactived = Arrays.copyOf(this._unactived, this._unactived.length + 1);
            this._active[this._active.length-1] = actived;
            this._unactived[this._unactived.length-1] = unactived;
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
        if (null != activeUpdater.get(this)) {
            try {
                activeInvokeCntUpdater.addAndGet(this, 1); // _activeCnt ++
                final Object[] impl = activeUpdater.get(this); // double check if active state?
                if (null != impl) {
                    // means _activeInvokeCnt > 0
                    return method.invoke(impl[idx], args);
                }
            } finally {
                if (0==activeInvokeCntUpdater.addAndGet(this, -1)) {
                    // _activeInvokeCnt -- , then equals 0
                    tryInvokeDelayTask();
                }
            }
        }
        final Object target = this._unactived[idx];
        return null != target ? method.invoke(target, args) : null;
    }
    
    public boolean isActive() {
        return null != activeUpdater.get(this);
    }
    
    public void destroyAndSubmit(final ActionN actionWhenDestroying, final Object... args) {
        final Object[] actived = activeUpdater.getAndSet(this, null);
        if (null!=actived) {
            // valid destroy call
            if (0 == activeInvokeCntUpdater.get(this)) {
                // means NONE active invoke processing
                safeInvokeAction(actionWhenDestroying, args);
                return;
            } else {
                // MAYBE _activeCnt > 0
                delayTaskUpdater.set(this, buildOnetimeAction(actionWhenDestroying,  args));
                if (0 == activeInvokeCntUpdater.get(this)) {
                    // active invoke all ended, and MAYBE not see the delay task, so try to invoke
                    tryInvokeDelayTask();
                }
            }
        }
    }

    private void tryInvokeDelayTask() {
        final Action0 delayTask = delayTaskUpdater.getAndSet(this, null);
        if (null != delayTask) {
            delayTask.call();
        }
    }

    private Action0 buildOnetimeAction(final ActionN actionWhenDestroying,
            final Object[] args) {
        final AtomicBoolean called = new AtomicBoolean(false);
        return new Action0() {
            @Override
            public void call() {
                if (called.compareAndSet(false, true)) {
                    safeInvokeAction(actionWhenDestroying, args);
                }
            }};
    }

    private static void safeInvokeAction(final ActionN action, final Object... args) {
        if (null!=action) {
            try {
                action.call(args);
            } catch (Exception e) {
                LOG.warn("exception when invoke action({}), detail: {}",
                    action, ExceptionUtils.exception2detail(e));
            }
        }
    }

    private static final AtomicReferenceFieldUpdater<InterfaceSelector, Object[]> activeUpdater =
            AtomicReferenceFieldUpdater.newUpdater(InterfaceSelector.class, Object[].class, "_active");

    private static final AtomicIntegerFieldUpdater<InterfaceSelector> activeInvokeCntUpdater =
            AtomicIntegerFieldUpdater.newUpdater(InterfaceSelector.class, "_activeInvokeCnt");
    
    private static final AtomicReferenceFieldUpdater<InterfaceSelector, Action0> delayTaskUpdater =
            AtomicReferenceFieldUpdater.newUpdater(InterfaceSelector.class, Action0.class, "_delayTask");
    
    private Object[] _unactived = null;
    private volatile Object[] _active = null;
    
    @SuppressWarnings("unused")
    private volatile int _activeInvokeCnt = 0;
    
    @SuppressWarnings("unused")
    private volatile Action0 _delayTask = null;
}
