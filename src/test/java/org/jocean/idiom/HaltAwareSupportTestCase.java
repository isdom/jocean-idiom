package org.jocean.idiom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import rx.functions.Action0;

public class HaltAwareSupportTestCase {

    @Test
    public final void testHaltAwareSupport() {
        final InterfaceSelector selector = new InterfaceSelector();
        final HaltAwareSupport<Object> support = new HaltAwareSupport<Object>(selector);
        final AtomicBoolean called = new AtomicBoolean(false);

        support.doOnHalt(null, new Action0() {
            @Override
            public void call() {
                called.set(true);
            }});

        assertFalse(called.get());

        support.fireAllActions(null);

        assertTrue(called.get());
    }

    @Test
    public final void testHaltAwareSupportAfterDestroyed() {
        final InterfaceSelector selector = new InterfaceSelector();
        final HaltAwareSupport<Object> support = new HaltAwareSupport<Object>(selector);

        selector.destroyAndSubmit(null);
        final AtomicBoolean called = new AtomicBoolean(false);

        support.doOnHalt(null, new Action0() {
            @Override
            public void call() {
                called.set(true);
            }});

        assertTrue(called.get());
    }
}
