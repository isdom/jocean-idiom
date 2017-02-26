package org.jocean.idiom;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import rx.functions.ActionN;

public class InterfaceSelectorTestCase {

    interface Demo {
        public int num();
    }
    
    @Test
    public final void testActiveAndUnactive() {
        final InterfaceSelector selector = new InterfaceSelector();
        
        final Demo demo = selector.build(Demo.class, new Demo() {
            @Override
            public int num() {
                return 1;
            }}, new Demo() {

            @Override
            public int num() {
                return 0;
            }});
        
        assertEquals(1, demo.num());
        
        selector.destroy(null);
        
        assertEquals(0, demo.num());
    }

    @Test
    public final void testUnactiveTwice() {
        final InterfaceSelector selector = new InterfaceSelector();
        
        final Demo demo = selector.build(Demo.class, new Demo() {
            @Override
            public int num() {
                return 1;
            }}, new Demo() {

            @Override
            public int num() {
                return 0;
            }});
        
        selector.destroy(null);
        
        assertEquals(0, demo.num());
        
        final AtomicBoolean called = new AtomicBoolean(false);

        selector.destroy(new ActionN() {
            @Override
            public void call(final Object... args) {
                called.set(true);
            }});
        
        assertEquals(0, demo.num());
        assertFalse(called.get());
    }
}
