package org.jocean.idiom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Func1;

public class Proxys {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(Proxys.class);
    
    private Proxys() {
        throw new IllegalStateException("No instances!");
    }

    @SuppressWarnings("unchecked")
    public static <T> T delegate(final Class<T> intf, final Object... delegates) {
        return (T) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(), 
            new Class<?>[]{intf}, new Handler(delegates));
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
            } catch (ClassNotFoundException e) {
                LOG.warn("exception when Class.forName({}), detail: {}", 
                        ss[0], ExceptionUtils.exception2detail(e));
            }
            return null;
        }});
    
    private static String classesNameOf(final Object[] delegates) {
        final StringBuilder sb = new StringBuilder();
        String splitter = "";
        for (Object o : delegates) {
            sb.append(splitter);
            sb.append(o.getClass().getName());
            splitter = "/";
        }
        return sb.toString();
    }

    private static class Handler implements InvocationHandler {

        public Handler(final Object... delegates) {
            this._delegates = delegates;
            this._classes = classesNameOf(delegates);
        }

        public Object invoke(final Object obj, final Method method, final Object[] args)
                throws Throwable {
            final String classesAndMethod = this._classes + ":" + method.getName();
            final Pair<Integer,Method> idxAndMethod = METHODS.get(classesAndMethod);
            if (null != idxAndMethod) {
                final Object delegate = this._delegates[idxAndMethod.first];
                final Method delegateMethod = idxAndMethod.second;
                final Object ret = delegateMethod.invoke(delegate, args);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("invoke ret: {}\r\n\tdelegateMethod: {}\r\n\tand Method: {}", 
                            ret, delegateMethod, method);
                }
                if (null != ret && (ret.getClass().equals(method.getReturnType()))) {
                    return ret;
                } else if (method.getReturnType().isInstance(obj)) {
                    return obj;
                } else {
                    LOG.warn("invoke ret: {}\r\n\tdelegateMethod: {}\r\n\tMethod: {}\r\n\tand invoke will return null", 
                            ret, delegateMethod, method);
                }
            }
            return null;
        }
        
        private final Object[] _delegates;
        private final String _classes;
    }

}
