package org.jocean.idiom;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProxysTestCase {

    public interface IA {
        public IA setA(final int a);
        public int getA();
    }
    
    static class A {
        public void setA(final int a) {
            this._a = a;
        }
        
        public int getA() {
            return this._a;
        }
        
        int _a;
    }
    
    @Test
    public final void testDelegate() {
        final A delegate = new A();
        final IA ia = Proxys.delegate(IA.class, delegate);
        
        assertSame(ia, ia.setA(100));
        
        assertEquals(100, delegate._a);

        assertEquals(100, ia.getA());
    }

    public interface IAB {
        public IAB setA(final int a);
        public int getA();
        public IAB setB(final long b);
        public long getB();
    }
    
    static class B {
        public void setB(final long b) {
            this._b = b;
        }
        
        public long getB() {
            return this._b;
        }
        
        long _b;
    }
    
    @Test
    public final void testMultiDelegate() {
        final A a = new A();
        final B b = new B();
        final IAB ab = Proxys.delegate(IAB.class, a, b);
        
        assertSame(ab, ab.setA(100));
        
        assertEquals(100, a._a);
        assertEquals(100, ab.getA());

        assertSame(ab, ab.setB(1L));
        
        assertEquals(1L, b._b);
        assertEquals(1L, ab.getB());
    }
}
