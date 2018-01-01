package org.jocean.idiom;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

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

    public interface IB {
        public int funcB();
    }
    
    @Test
    public final void testMixin() {
        final AtomicInteger _a = new AtomicInteger(0);
        final IA ia = Proxys.mixin()
                .mix(IA.class, new IA() {
                    @Override
                    public IA setA(int a) {
                        System.out.println("IA's setA");
                        _a.set(a);
                        return null;
                    }
        
                    @Override
                    public int getA() {
                        System.out.println("IA's getA");
                        return _a.get();
                    }})
                .mix(IB.class, new IB() {

                    @Override
                    public int funcB() {
                        System.out.println("IB's funcB");
                        return _a.get();
                    }})
                .build();
        ia.setA(1000);
        assertEquals(1000, ((IB)ia).funcB());
    }
}
