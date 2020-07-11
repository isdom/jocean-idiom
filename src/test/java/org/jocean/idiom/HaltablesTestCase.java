/**
 *
 */
package org.jocean.idiom;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author isdom
 *
 */
public class HaltablesTestCase {

    /**
     * Test method for {@link org.jocean.idiom.Haltables#extend(org.jocean.idiom.Haltable, long, java.util.concurrent.TimeUnit)}.
     * @throws InterruptedException
     */
    @Test
    public final void testExtend() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);

        final long start = System.currentTimeMillis();
        Haltables.extend(Haltables.delay(5, TimeUnit.SECONDS), 5, TimeUnit.SECONDS).doOnHalt(() -> cdl.countDown());

        cdl.await();
        final long duration = System.currentTimeMillis() - start;
        System.out.println("duration in ms:" + duration );
        assertTrue( duration >= 10000 && duration <= 11000);
    }

}
