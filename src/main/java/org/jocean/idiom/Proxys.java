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
    public static <T> T build(final Class<T> intf, final Object delegate) {
        return (T) Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(), new Class<?>[]{intf}, new Handler(delegate));
    }
    
    private final static SimpleCache<String, Method> METHODS = new SimpleCache<>(new Func1<String, Method>() {
        @Override
        public Method call(final String clazzAndMethod) {
            final String[] ss = clazzAndMethod.split(":");
            try {
                final Class<?> clazz = Class.forName(ss[0]);
                return ReflectUtils.getMethodNamed(clazz, ss[1]);
            } catch (ClassNotFoundException e) {
                LOG.warn("exception when Class.forName({}), detail: {}", 
                        ss[0], ExceptionUtils.exception2detail(e));
                return null;
            }
        }});
    
    private static class Handler implements InvocationHandler {

        public Handler(final Object delegate) {
            this._delegate = delegate;
        }

        public Object invoke(final Object obj, final Method method, final Object[] args)
                throws Throwable {
            final String clazzAndMethod = this._delegate.getClass().getName() + ":" + method.getName();
            final Method implMethod = METHODS.get(clazzAndMethod);
            if (null != implMethod) {
                final Object ret = implMethod.invoke(this._delegate, args);
                if (implMethod.getReturnType().equals(method.getReturnType())) {
                    return ret;
                } else if (method.getReturnType().isInstance(obj)) {
                    return obj;
                }
            }
            return null;
        }
        
        private final Object _delegate;
    }

}
