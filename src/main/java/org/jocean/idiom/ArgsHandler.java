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

        public static class PairedArgsGuard implements ArgsHandler {
            public PairedArgsGuard(final PairedVisitor<Object> paired) {
                this._paired = paired;
            }
            
            @Override
            public Object[] beforeInvoke(final Object[] args) {
                if ( null != args ) {
                    for ( Object arg : args) {
                        try {
                            this._paired.visitBegin(arg);
                        }
                        catch (Throwable e) {
                            LOG.error("exception when invoke PairedVisitor({})'s visitBegin for arg({}), detail:{}",
                                    this._paired, arg, ExceptionUtils.exception2detail(e));
                        }
                    }
                }
                return args;
            }
    
            @Override
            public void afterInvoke(final Object[] args) {
                if ( null != args ) {
                    for ( Object arg : args) {
                        try {
                            this._paired.visitEnd(arg);
                        }
                        catch (Throwable e) {
                            LOG.error("exception when invoke PairedVisitor({})'s visitEnd for arg({}), detail:{}",
                                    this._paired, arg, ExceptionUtils.exception2detail(e));
                        }
                    }
                }
            }
            
            private final PairedVisitor<Object> _paired;
        }
        
        public static final ArgsHandler _REFCOUNTED_ARGS_GUARD = 
                new PairedArgsGuard(ReferenceCounted.Utils._REFCOUNTED_GUARD);
	}
}
