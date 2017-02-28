package org.jocean.idiom;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import rx.functions.ActionN;

public class InterfaceSelectorTestCase {

    interface Demo {
        public int num();
    }
    
    private static final ActionN SET_TO_TRUE = new ActionN() {
        @Override
        public void call(final Object... args) {
            ((AtomicBoolean)args[0]).set(true);
        }};
        
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
        
        selector.destroyAndSubmit(null);
        
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
        
        selector.destroyAndSubmit(null);
        
        assertEquals(0, demo.num());
        
        final AtomicBoolean called = new AtomicBoolean(false);

        selector.destroyAndSubmit(SET_TO_TRUE, called);
        
        assertEquals(0, demo.num());
        assertFalse(called.get());
    }

    @Test
    public final void testUnactiveWithinActiveCall() {
        final InterfaceSelector selector = new InterfaceSelector();
        
        final AtomicBoolean called = new AtomicBoolean(false);
        final Demo demo = selector.build(Demo.class, new Demo() {
            @Override
            public int num() {
                selector.destroyAndSubmit(SET_TO_TRUE, called);
                assertFalse(called.get());
                return 1;
            }}, new Demo() {

            @Override
            public int num() {
                return 0;
            }});
        
        assertEquals(1, demo.num());
        assertTrue(called.get());
        assertEquals(0, demo.num());
    }
}
