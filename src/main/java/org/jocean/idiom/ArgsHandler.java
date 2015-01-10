/**
 * 
 */
package org.jocean.idiom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author isdom
 *
 */
public interface ArgsHandler {
	
	public Object[] beforeInvoke(final Object[] args) throws Exception;
	
	public void afterInvoke(final Object[] args) throws Exception;
	
	final class Consts {
	    private static final Logger LOG = 
	            LoggerFactory.getLogger(Consts.class);

	    public static class RefcountedArgsGuard implements ArgsHandler {
            @Override
            public Object[] beforeInvoke(final Object[] args) {
                if ( null != args ) {
                    for ( Object arg : args) {
                        if ( arg instanceof ReferenceCounted ) {
                            try {
                                ((ReferenceCounted<?>)arg).retain();
                            }
                            catch (Throwable e) {
                                LOG.error("exception when invoke ReferenceCounted({})'s retain, detail:{}",
                                        arg, ExceptionUtils.exception2detail(e));
                            }
                        }
                    }
                }
                return args;
            }
    
            @Override
            public void afterInvoke(final Object[] args) {
                if ( null != args ) {
                    for ( Object arg : args) {
                        if ( arg instanceof ReferenceCounted ) {
                            try {
                                ((ReferenceCounted<?>)arg).release();
                            }
                            catch (Throwable e) {
                                LOG.error("exception when invoke ReferenceCounted({})'s release, detail:{}",
                                        arg, ExceptionUtils.exception2detail(e));
                            }
                        }
                    }
                }
            }	        
	    }
	    
        public static final ArgsHandler _REFCOUNTED_ARGS_GUARD = new RefcountedArgsGuard();

        public static class PairedArgsGuard implements ArgsHandler {
            public PairedArgsGuard(final Paired paired) {
                this._paired = paired;
            }
            
            @Override
            public Object[] beforeInvoke(final Object[] args) {
                if ( null != args ) {
                    for ( Object arg : args) {
                        if ( null != arg && this._paired.pairedClass().isAssignableFrom(arg.getClass())) {
                            try {
                                this._paired.beginMethod().invoke(arg);
                            }
                            catch (Throwable e) {
                                LOG.error("exception when invoke Paired({})'s beginMethod({}), detail:{}",
                                        arg, this._paired.beginMethod(), ExceptionUtils.exception2detail(e));
                            }
                        }
                    }
                }
                return args;
            }
    
            @Override
            public void afterInvoke(final Object[] args) {
                if ( null != args ) {
                    for ( Object arg : args) {
                        if ( null != arg && this._paired.pairedClass().isAssignableFrom(arg.getClass())) {
                            try {
                                this._paired.endMethod().invoke(arg);
                            }
                            catch (Throwable e) {
                                LOG.error("exception when invoke Paired({})'s endMethod({}), detail:{}",
                                        arg, this._paired.endMethod(), ExceptionUtils.exception2detail(e));
                            }
                        }
                    }
                }
            }
            
            private final Paired _paired;
        }
	}
}
