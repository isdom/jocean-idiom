/**
 * 
 */
package org.jocean.idiom;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author isdom
 *
 */
public class ExceptionUtils {
	
	public static String exception2detail(final Throwable e) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final PrintWriter writer = new PrintWriter(os);
		e.printStackTrace(writer);
		writer.flush();
		
		try {
			return os.toString("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			//e1.printStackTrace();
			return e.toString();
		}
	}

	public static String dumpCallStack(final Throwable throwable, final String prefix, int skipDepth) {
	    final StringBuilder sb = new StringBuilder();
        final StackTraceElement[] callStacks = throwable.getStackTrace();

        if ( null != prefix ) {
            sb.append(prefix);
        }
        
        for ( StackTraceElement cs : callStacks) {
            if ( skipDepth > 0 ) {
                skipDepth--;
                continue;
            }
            sb.append('\r');
            sb.append('\n');
            sb.append("\tat ");
            sb.append(cs);
        }
        
        return sb.toString();
	}
}
