package org.jocean.idiom;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import rx.functions.Action0;

public class TerminateAwareSupportTestCase {

    @Test
    public final void testTerminateAwareSupport() {
        final InterfaceSelector selector = new InterfaceSelector();
        final TerminateAwareSupport<Object> support = 
                new TerminateAwareSupport<Object>(selector);
        final AtomicBoolean called = new AtomicBoolean(false);
        
        support.doOnTerminate(null, new Action0() {
            @Override
            public void call() {
                called.set(true);
            }});
        
        assertFalse(called.get());
        
        support.fireAllTerminates(null);
        
        assertTrue(called.get());
    }

    @Test
    public final void testTerminateAwareSupportAfterDestroyed() {
        final InterfaceSelector selector = new InterfaceSelector();
        final TerminateAwareSupport<Object> support = 
                new TerminateAwareSupport<Object>(selector);
        
        selector.destroyAndSubmit(null);
        final AtomicBoolean called = new AtomicBoolean(false);
        
        support.doOnTerminate(null, new Action0() {
            @Override
            public void call() {
                called.set(true);
            }});
        
        assertTrue(called.get());
    }
}
