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
    public final void testBuild() {
        final A delegate = new A();
        final IA ia = Proxys.build(IA.class, delegate);
        
        assertSame(ia, ia.setA(100));
        
        assertEquals(100, delegate._a);

        assertEquals(100, ia.getA());
    }

}
