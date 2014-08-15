/**
 * 
 */
package org.jocean.idiom;


/**
 * @author isdom
 *
 */
public interface ArgsHandler {
	
	public Object[] beforeInvoke(final Object[] args) throws Exception;
	
	public void afterInvoke(final Object[] args) throws Exception;

	final class Consts {
	    public static class RefcountedArgsGuard implements ArgsHandler {
            @Override
            public Object[] beforeInvoke(final Object[] args) {
                if ( null != args ) {
                    for ( Object arg : args) {
                        if ( arg instanceof ReferenceCounted ) {
                            ((ReferenceCounted<?>)arg).retain();
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
                            ((ReferenceCounted<?>)arg).release();
                        }
                    }
                }
            }	        
	    }
	    
        public static final ArgsHandler _REFCOUNTED_ARGS_GUARD = new RefcountedArgsGuard();
	}
}
