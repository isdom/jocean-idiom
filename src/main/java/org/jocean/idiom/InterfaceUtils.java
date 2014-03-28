/**
 * 
 */
package org.jocean.idiom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author isdom
 *
 */
public class InterfaceUtils {
	
	private static final Logger LOG = 
        	LoggerFactory.getLogger(InterfaceUtils.class);
	
	@SuppressWarnings("unchecked")
	public static <T> T combineImpls(final Class<T> cls, final T ... impls) {
		return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
				new Class<?>[]{cls}, new CompositeImplHandler(impls));
	}

	private static final class CompositeImplHandler<T> implements InvocationHandler {

		CompositeImplHandler(final T ... impls) {
			if ( null == impls || 0 == impls.length ) {
				throw new NullPointerException("impl can't be neither null nor empty array");
			}
			this._impls = impls;
		}
		
		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args)
				throws Throwable {
			// An invocation of the hashCode, equals, or toString methods declared in java.lang.Object 
			//	on a proxy instance will be encoded and dispatched to the invocation handler's invoke 
			//	method in the same manner as interface method invocations are encoded and dispatched, 
			//	as described above. The declaring class of the Method object passed to invoke will be 
			//	java.lang.Object. Other public methods of a proxy instance inherited from java.lang.Object 
			//	are not overridden by a proxy class, so invocations of those methods behave like they do 
			//	for instances of java.lang.Object.			
			if ( method.getName().equals("hashCode") ) {
				return	Arrays.hashCode(this._impls);
			}
			else if (method.getName().equals("equals") ) {
				return	(proxy == args[0]);
			}
			else if (method.getName().equals("toString") ) {
				return	Arrays.toString(this._impls);
			}
			Object ret = null;
			for ( T impl : this._impls ) {
				try {
					ret = method.invoke(impl, args);
				}
				catch (Exception e) {
					LOG.warn("exception when invoke method {} by impl {}, detail: {}", 
							method, impl, ExceptionUtils.exception2detail(e));
				}
			}
			//	return last impl's return value
			return	ret;
		}
		
		private final T[] _impls;
	}
}
