package org.jocean.idiom;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReflectUtilsTestCase {

	class Inner1 {
		class Inner2 {
		}
		
		Inner2 inner2 = new Inner2();
	};
	
	@Test
	public void testGetOuterFromInnerObjectForInner1() {
		final ReflectUtilsTestCase case1 = ReflectUtils.getOuterFromInnerObject(new Inner1());
		
		assertNotNull(case1);
	}

	@Test
	public void testGetOuterFromInnerObjectForInner2() {
		final Inner1 inner1 = new Inner1();
		final Inner1 innerRet = ReflectUtils.getOuterFromInnerObject(inner1.inner2);
		
		assertSame(innerRet, inner1);
	}
}
