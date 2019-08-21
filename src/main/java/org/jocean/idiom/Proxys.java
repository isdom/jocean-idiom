package org.jocean.idiom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Func1;

public class Proxys {
    public static enum RET {
        PASSTHROUGH,
        SELF
    }

    private static final Logger LOG = LoggerFactory.getLogger(Proxys.class);

    private Proxys() {
        throw new IllegalStateException("No instances!");
    }

    @SuppressWarnings("unchecked")
    public static <T> T delegate(final Class<T> intf, final Object... delegates) {
        // default return mode is pass-through delegate's return value
        final RET[] rets = new RET[delegates.length];
        Arrays.fill(rets, RET.PASSTHROUGH);
        return (T) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{intf}, new DelegateHandler(delegates, rets));
    }

    @SuppressWarnings("unchecked")
    public static <T> T delegate(final Class<T> intf, final Object[] delegates, final RET[] rets) {
        return (T) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{intf}, new DelegateHandler(delegates, rets));
    }

    private final static SimpleCache<String, Pair<Integer,Method>> METHODS = new SimpleCache<>(new Func1<String, Pair<Integer,Method>>() {
        @Override
        public Pair<Integer,Method> call(final String classesAndMethod) {
            final String[] ss = classesAndMethod.split(":");
            final String[] classes = ss[0].split("/");
            try {
                for (int idx = 0; idx < classes.length; idx++) {
                    final Class<?> clazz = Class.forName(classes[idx]);
                    final Method method = ReflectUtils.getMethodNamed(clazz, ss[1]);
                    if (null != method) {
                        return Pair.of(idx, method);
                    }
                }
            } catch (final ClassNotFoundException e) {
                LOG.warn("exception when Class.forName({}), detail: {}",
                        ss[0], ExceptionUtils.exception2detail(e));
            }
            return null;
        }});

    private static String classesNameOf(final Object[] delegates) {
        final StringBuilder sb = new StringBuilder();
        String splitter = "";
        for (final Object o : delegates) {
            sb.append(splitter);
            sb.append(o.getClass().getName());
            splitter = "/";
        }
        return sb.toString();
    }

    private static class DelegateHandler implements InvocationHandler {

        public DelegateHandler(final Object[] delegates, final RET[] rets) {
            if (delegates.length != rets.length) {
                throw new RuntimeException("delegates and rets's size NOT match!");
            }
            this._delegates = delegates;
            this._rets = rets;
            this._classes = classesNameOf(delegates);
        }

        @Override
        public Object invoke(final Object obj, final Method method, final Object[] args)
                throws Throwable {
            //   An invocation of the hashCode, equals, or toString methods
            // declared in java.lang.Object on a proxy instance will be
            // encoded and dispatched to the invocation handler's invoke
            // method in the same manner as interface method invocations are
            // encoded and dispatched, as described above. The declaring
            // class of the Method object passed to invoke will be
            // java.lang.Object. Other public methods of a proxy instance
            // inherited from java.lang.Object are not overridden by a proxy
            // class, so invocations of those methods behave like they do
            // for instances of java.lang.Object.
            if (method.getName().equals("hashCode")) {
                return this._delegates[0].hashCode();
            } else if (method.getName().equals("equals")) {
                return (obj == args[0]);
            } else if (method.getName().equals("toString")) {
                return this._delegates[0].toString();
            }

            final String classesAndMethod = this._classes + ":" + method.getName();
            final Pair<Integer,Method> idxAndMethod = METHODS.get(classesAndMethod);
            if (null != idxAndMethod) {
                final Object delegate = this._delegates[idxAndMethod.first];
                final RET retmode = this._rets[idxAndMethod.first];
                final Method delegateMethod = idxAndMethod.second;
                final Object ret = delegateMethod.invoke(delegate, args);
                if (retmode.equals(RET.PASSTHROUGH)) {
                    return ret;
                } else if (retmode.equals(RET.SELF)) {
                    return obj;
                } else {
                    throw new RuntimeException("Unknow return mode: " + retmode);
                }
            }
            return null;
        }

        private final Object[] _delegates;
        private final RET[] _rets;
        private final String _classes;
    }

    public static interface MixinBuilder {
        public <T> MixinBuilder mix(final Class<T> type, final T obj);
        public <T> T build();
    }

    public static MixinBuilder mixin() {
        final Map<Class<?>, Object> _mixin = new HashMap<>();
        return new MixinBuilder() {

            @Override
            public <T> MixinBuilder mix(final Class<T> type, final T target) {
                _mixin.put(type, target);
                return this;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> T build() {
                return (T) Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        _mixin.keySet().toArray(new Class<?>[0]),
                        new MixinHandler(_mixin));
            }};
    }

    private static class MixinHandler implements InvocationHandler {

        public MixinHandler(final Map<Class<?>, Object> mixin) {
            this._mixin = mixin;
        }

        @Override
        public Object invoke(final Object obj, final Method method, final Object[] args)
                throws Throwable {
            //   An invocation of the hashCode, equals, or toString methods
            // declared in java.lang.Object on a proxy instance will be
            // encoded and dispatched to the invocation handler's invoke
            // method in the same manner as interface method invocations are
            // encoded and dispatched, as described above. The declaring
            // class of the Method object passed to invoke will be
            // java.lang.Object. Other public methods of a proxy instance
            // inherited from java.lang.Object are not overridden by a proxy
            // class, so invocations of those methods behave like they do
            // for instances of java.lang.Object.
            if (method.getName().equals("hashCode")) {
                return this._mixin.hashCode();
            } else if (method.getName().equals("equals")) {
                return (obj == args[0]);
            } else if (method.getName().equals("toString")) {
                return this._mixin.toString();
            }

            final Object target = this._mixin.get(method.getDeclaringClass());
            if (null != target) {
                return method.invoke(target, args);
            } else {
                throw new RuntimeException("invalid method(" + method +") invoke");
            }
        }

        private final Map<Class<?>, Object> _mixin;
    }
}
