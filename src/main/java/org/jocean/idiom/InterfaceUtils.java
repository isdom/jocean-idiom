/**
 * 
 */
package org.jocean.idiom;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Func1;
import rx.internal.util.PlatformDependent;

/**
 * @author isdom
 *
 */
public class InterfaceUtils {
	
	private static final Logger LOG = 
        	LoggerFactory.getLogger(InterfaceUtils.class);
	
    @SuppressWarnings("unchecked")
    public static <SRC,T> T compositeBySource(
            final Class<T>          intfType,
            final Func1<SRC,T>      src2intf, 
            final SRC...            sources) {
        final List<T> intfs = new ArrayList<>();
        for (SRC src : sources) {
            final T intf = src2intf.call(src);
            if (null!=intf) {
                intfs.add(intf);
            }
        }
        return !intfs.isEmpty() 
                ? combineImpls(intfType, intfs.toArray((T[])Array.newInstance(intfType, 0))) 
                : null;
    }
    
    public static <T> T compositeIncludeType(final Class<T> cls, final Object... objs) {
    	final T[] impls = selectIncludeType(cls, objs);
    	return (null != impls) 
    		? combineImpls(cls, impls)
    		: null;
    }
    
    @SuppressWarnings("unchecked")
    public static <T,EX> T compositeExcludeType(final Class<T> cls, final Class<EX> exCls, final Object... objs) {
        final Object[] impls = selectExcludeType(exCls, objs);
        return (null != impls) 
                ? combineImpls(cls, Arrays.asList(impls).toArray((T[])Array.newInstance(cls, 0)))
                : null;
    }
    
	@SuppressWarnings("unchecked")
	public static <T> T[] selectIncludeType(final Class<T> cls, final Object... objs) {
		final List<T> objsOfT = new ArrayList<T>() {
			private static final long serialVersionUID = 1L;
		{
			for (Object obj : objs) {
				if (null != obj && cls.isAssignableFrom(obj.getClass())) {
					this.add((T)obj);
				}
			}
		}};
		return !objsOfT.isEmpty() ? objsOfT.toArray((T[])Array.newInstance(cls, 0)) : null;
	}
	
    public static <T> Object[] selectExcludeType(final Class<T> cls, final Object... objs) {
        final List<Object> objsOfT = new ArrayList<Object>() {
            private static final long serialVersionUID = 1L;
        {
            for (Object obj : objs) {
                if (null != obj && !cls.isAssignableFrom(obj.getClass())) {
                    this.add(obj);
                }
            }
        }};
        return !objsOfT.isEmpty() ? objsOfT.toArray() : null;
    }
    
	@SuppressWarnings("unchecked")
	public static <T> T combineImpls(final Class<T> cls, final T ... impls) {
        final ClassLoader cl = getClassLoader(cls);
        if (LOG.isTraceEnabled()) {
            LOG.trace("combineImpls: using ClassLoader: {}", cl);
        }
        
        return (T) Proxy.newProxyInstance(cl,
				new Class<?>[]{cls}, new CompositeImplHandler<T>(impls));
	}

	private static ClassLoader getClassLoader(final Class<?> cls) {
	    if (PlatformDependent.isAndroid()) {
	        return cls.getClassLoader();
	    } else {
	        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
	        if (cl == null) {
	            return cls.getClassLoader();
	        } else {
	            return cl;
	        }
	    }
	}
	
	private static final class CompositeImplHandler<T> implements InvocationHandler {

		@SafeVarargs
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
	
    @SuppressWarnings("unchecked")
    public static <T> T genAsyncImpl(
            final Class<T> cls, final T impl, 
            final ExectionLoop exectionLoop,
            final ArgsHandler argsHandler) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = cls.getClassLoader();
        }
        return (T) Proxy.newProxyInstance(cl,
                new Class<?>[]{cls}, new AsyncImplHandler<T>(impl, exectionLoop, argsHandler));
    }

    private static final class AsyncImplHandler<T> implements InvocationHandler {

        AsyncImplHandler(
                final T impl, 
                final ExectionLoop exectionLoop, 
                final ArgsHandler argsHandler) {
            if ( null == impl || null == exectionLoop ) {
                throw new NullPointerException("impl or exectionLoop is null.");
            }
            this._impl = impl;
            this._exectionLoop = exectionLoop;
            this._argsHandler = argsHandler;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args)
                throws Throwable {
            // An invocation of the hashCode, equals, or toString methods declared in java.lang.Object 
            //  on a proxy instance will be encoded and dispatched to the invocation handler's invoke 
            //  method in the same manner as interface method invocations are encoded and dispatched, 
            //  as described above. The declaring class of the Method object passed to invoke will be 
            //  java.lang.Object. Other public methods of a proxy instance inherited from java.lang.Object 
            //  are not overridden by a proxy class, so invocations of those methods behave like they do 
            //  for instances of java.lang.Object.          
            if ( method.getName().equals("hashCode") ) {
                return  this._impl.hashCode();
            }
            else if (method.getName().equals("equals") ) {
                return  (proxy == args[0]);
            }
            else if (method.getName().equals("toString") ) {
                return  this._impl.toString();
            }
            
            doBeforeInvoke(args);
            if ( this._exectionLoop.inExectionLoop() ) {
                try {
                    return method.invoke(this._impl, args);
                }
                finally {
                    doAfterInvoke(args);
                }
            }
            else {
                this._exectionLoop.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(_impl, args);
                        }
                        catch(Exception e) {
                            LOG.warn("exception when invoke method({}) for impl({}), detail:{}",
                                    method.getName(), _impl, ExceptionUtils.exception2detail(e));
                        }
                        finally {
                            doAfterInvoke(args);
                        }
                    }});
                return  null;
            }
        }

        private void doBeforeInvoke(final Object[] args) {
            if ( null != this._argsHandler ) {
                try {
                    this._argsHandler.beforeInvoke(args);
                }
                catch (Throwable e) {
                    LOG.warn("exception when invoke beforeAcceptEvent for ({}), detail: {}",
                            this._impl.toString(), ExceptionUtils.exception2detail(e));
                }
            }
        }
        
        private void doAfterInvoke(final Object[] args) {
            if ( null != this._argsHandler ) {
                try {
                    this._argsHandler.afterInvoke(args);
                }
                catch (Throwable e) {
                    LOG.warn("exception when invoke afterAcceptEvent for ({}), detail: {}",
                            this._impl.toString(), ExceptionUtils.exception2detail(e));
                }
            }
        }
        
        private final T _impl;
        private final ExectionLoop _exectionLoop;
        private final ArgsHandler _argsHandler;
    }
}
