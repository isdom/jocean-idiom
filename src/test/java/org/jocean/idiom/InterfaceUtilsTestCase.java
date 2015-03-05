package org.jocean.idiom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class InterfaceUtilsTestCase {

	public interface Intf1 {
	};
	
	@Test
	public void testFilterByType() {
		
		final Object[] objs = new Object[]{new Object(), new Intf1() {}, new Object()};
		final Intf1[] filtered = InterfaceUtils.filterByType(objs, Intf1.class);
		
		assertEquals(1, filtered.length);
	}

	@Test
	public void testFilterByTypeRetNull() {
		
		final Object[] objs = new Object[]{new Object(), new Object(), new Object()};
		final Intf1[] filtered = InterfaceUtils.filterByType(objs, Intf1.class);
		
		assertNull(filtered);
	}
}
