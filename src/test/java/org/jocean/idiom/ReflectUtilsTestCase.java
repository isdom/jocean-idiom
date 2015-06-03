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

	static public class A implements Cloneable {

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
        
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + i;
            return result;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            A other = (A) obj;
            if (i != other.i)
                return false;
            return true;
        }


        private final int i = 10;
	}
	
    @Test
    public void testClone() {
        final A a = new A();
        
        final A clonedA = ReflectUtils.invokeClone(a);
        
        assertEquals(a, clonedA);
    }
}
