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

}
