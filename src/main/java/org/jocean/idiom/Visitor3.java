/**
 * 
 */
package org.jocean.idiom;

/**
 * @author isdom
 *
 */
public interface Visitor3<FIRST, SECOND, THIRD> {
	public void visit(final FIRST first, final SECOND second, final THIRD third) 
			throws Exception;
}
