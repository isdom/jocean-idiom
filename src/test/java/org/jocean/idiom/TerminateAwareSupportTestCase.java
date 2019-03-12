package org.jocean.idiom;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import rx.functions.Action0;

public class TerminateAwareSupportTestCase {

    @Test
    public final void testTerminateAwareSupport() {
        final InterfaceSelector selector = new InterfaceSelector();
        final EndAwareSupport<Object> support = 
                new EndAwareSupport<Object>(selector);
        final AtomicBoolean called = new AtomicBoolean(false);
        
        support.doOnEnd(null, new Action0() {
            @Override
            public void call() {
                called.set(true);
            }});
        
        assertFalse(called.get());
        
        support.fireAllActions(null);
        
        assertTrue(called.get());
    }

    @Test
    public final void testTerminateAwareSupportAfterDestroyed() {
        final InterfaceSelector selector = new InterfaceSelector();
        final EndAwareSupport<Object> support = 
                new EndAwareSupport<Object>(selector);
        
        selector.destroyAndSubmit(null);
        final AtomicBoolean called = new AtomicBoolean(false);
        
        support.doOnEnd(null, new Action0() {
            @Override
            public void call() {
                called.set(true);
            }});
        
        assertTrue(called.get());
    }
}
