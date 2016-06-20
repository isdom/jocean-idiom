/**
 * 
 */
package org.jocean.idiom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author isdom
 *
 */
public class ProxyBuilder<T> {
    private static final Logger LOG =
            LoggerFactory.getLogger(ProxyBuilder.class);

    private AtomicReference<T> _ref = new AtomicReference<>();
    private Class<T>[] _intfs;

    private class Handler implements InvocationHandler {

        public Object invoke(Object obj, Method method, Object[] args)
                throws Throwable {
            T impl = _ref.get();
            if (null != impl) {
                return method.invoke(impl, args);
            }

            throw new RuntimeException("implementation Object !NOT! set yet.");
        }

    }

    private Handler _handler = new Handler();

    public ProxyBuilder(final Class<T> intf) {
        this(intf, null);
    }

    @SuppressWarnings("unchecked")
    public ProxyBuilder(final Class<T> intf, final T impl) {
        this._intfs = new Class[]{intf};
        this._ref.set(impl);
    }
    
    public ProxyBuilder(final Class<T>[] intfs) {
        this._intfs = Arrays.copyOf(intfs, intfs.length);
    }

    @SuppressWarnings("unchecked")
    public T buildProxy() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (LOG.isDebugEnabled()) {
            LOG.debug("using ClassLoader {} to newProxyInstance", cl);
        }
        return (T) Proxy.newProxyInstance(
                cl, this._intfs, this._handler);
    }

    public void setImpl(final T impl) {
        this._ref.set(impl);
    }

    public String toString() {
        T impl = this._ref.get();

        if (null != impl) {
            return impl.toString();
        }

        return "ProxyBuilder with null impl";
    }
}
