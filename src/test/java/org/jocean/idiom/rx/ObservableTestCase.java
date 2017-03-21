package org.jocean.idiom.rx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.rx.SubscriberHolder;
import org.jocean.idiom.rx.TestUtil;
import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;

public class ObservableTestCase {
    @Test
    public final void testFlatMapTwice() throws InterruptedException {
        final AtomicBoolean unsubscribed1 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed2 = new AtomicBoolean(false);
        final SubscriberHolder<String> holder1 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder2 = new SubscriberHolder<String>();
        
        TestUtil.createObservableByHolder(unsubscribed1, holder1)
        .flatMap(TestUtil.flatMapFuncOf(unsubscribed2, holder2))
        .subscribe();
        
        assertEquals(1, holder1.getSubscriberCount());
        assertEquals(0, holder2.getSubscriberCount());
        
        holder1.getAt(0).onNext("hello");
        holder1.getAt(0).onCompleted();
        
        assertFalse(unsubscribed1.get());
        
        assertEquals(1, holder2.getSubscriberCount());
        holder2.getAt(0).onNext("hello");
        
        assertFalse(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
        holder2.getAt(0).onCompleted();
        
        assertTrue(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
    }

    @Test
    public final void testFlatMapTwice2() throws InterruptedException {
        final AtomicBoolean unsubscribed1 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed2 = new AtomicBoolean(false);
        final SubscriberHolder<String> holder1 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder2 = new SubscriberHolder<String>();
        
        final Subscription subscription = 
                TestUtil.createObservableByHolder(unsubscribed1, holder1)
                .flatMap(TestUtil.flatMapFuncOf(unsubscribed2, holder2))
                .subscribe();
        
        assertEquals(1, holder1.getSubscriberCount());
        assertEquals(0, holder2.getSubscriberCount());
        
        holder1.getAt(0).onNext("hello");
        holder1.getAt(0).onCompleted();
        
        assertFalse(unsubscribed1.get());
        
        assertEquals(1, holder2.getSubscriberCount());
        
        subscription.unsubscribe();
        
        assertTrue(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
    }

    @Test
    public final void testFlatMapTwice3() throws InterruptedException {
        final AtomicBoolean unsubscribed1 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed2 = new AtomicBoolean(false);
        final SubscriberHolder<String> holder1 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder2 = new SubscriberHolder<String>();
        
        final Subscription subscription = 
                TestUtil.createObservableByHolder(unsubscribed1, holder1)
                .flatMap(TestUtil.flatMapFuncOf(unsubscribed2, holder2))
                .subscribe();
        
        assertEquals(1, holder1.getSubscriberCount());
        assertEquals(0, holder2.getSubscriberCount());
        
        subscription.unsubscribe();
        
        assertTrue(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
    }

    @Test
    public final void testFlatMapTriple() throws InterruptedException {
        final AtomicBoolean unsubscribed1 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed2 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed3 = new AtomicBoolean(false);
        final SubscriberHolder<String> holder1 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder2 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder3 = new SubscriberHolder<String>();
        
        TestUtil.createObservableByHolder(unsubscribed1, holder1)
        .flatMap(TestUtil.flatMapFuncOf(unsubscribed2, holder2))
        .flatMap(TestUtil.flatMapFuncOf(unsubscribed3, holder3))
        .subscribe();
        
        assertEquals(1, holder1.getSubscriberCount());
        assertEquals(0, holder2.getSubscriberCount());
        assertEquals(0, holder3.getSubscriberCount());
        
        holder1.getAt(0).onNext("hello");
        holder1.getAt(0).onCompleted();
        
        assertFalse(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        
        assertEquals(1, holder2.getSubscriberCount());
        assertEquals(0, holder3.getSubscriberCount());
        holder2.getAt(0).onNext("world");
        holder2.getAt(0).onCompleted();
        
        assertFalse(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        
        assertEquals(1, holder3.getSubscriberCount());
        holder3.getAt(0).onNext("rx");
        holder3.getAt(0).onCompleted();
        
        assertTrue(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
        assertTrue(unsubscribed3.get());
    }
    
    @Test
    public final void testFlatMapQuadruple() throws InterruptedException {
        final AtomicBoolean unsubscribed1 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed2 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed3 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed4 = new AtomicBoolean(false);
        final SubscriberHolder<String> holder1 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder2 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder3 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder4 = new SubscriberHolder<String>();
        
        TestUtil.createObservableByHolder(unsubscribed1, holder1)
        .flatMap(TestUtil.flatMapFuncOf(unsubscribed2, holder2))
        .flatMap(TestUtil.flatMapFuncOf(unsubscribed3, holder3))
        .flatMap(TestUtil.flatMapFuncOf(unsubscribed4, holder4))
        .subscribe();
        
        assertEquals(1, holder1.getSubscriberCount());
        assertEquals(0, holder2.getSubscriberCount());
        assertEquals(0, holder3.getSubscriberCount());
        assertEquals(0, holder4.getSubscriberCount());
        
        holder1.getAt(0).onNext("hello");
        holder1.getAt(0).onCompleted();
        
        assertFalse(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        assertFalse(unsubscribed4.get());
        
        assertEquals(1, holder2.getSubscriberCount());
        assertEquals(0, holder3.getSubscriberCount());
        assertEquals(0, holder4.getSubscriberCount());
        holder2.getAt(0).onNext("world");
        holder2.getAt(0).onCompleted();
        
        assertFalse(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        assertFalse(unsubscribed4.get());
        
        assertEquals(1, holder3.getSubscriberCount());
        assertEquals(0, holder4.getSubscriberCount());
        holder3.getAt(0).onNext("rx");
        holder3.getAt(0).onCompleted();
        
        assertFalse(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
        assertTrue(unsubscribed3.get());
        assertFalse(unsubscribed4.get());
        
        assertEquals(1, holder4.getSubscriberCount());
        holder4.getAt(0).onNext("java");
        holder4.getAt(0).onCompleted();
        
        assertTrue(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
        assertTrue(unsubscribed3.get());
        assertTrue(unsubscribed4.get());
    }

    @Test
    public final void testFlatMapQuadrupleDelayOnCompleted() throws InterruptedException {
        final AtomicBoolean unsubscribed1 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed2 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed3 = new AtomicBoolean(false);
        final AtomicBoolean unsubscribed4 = new AtomicBoolean(false);
        final SubscriberHolder<String> holder1 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder2 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder3 = new SubscriberHolder<String>();
        final SubscriberHolder<String> holder4 = new SubscriberHolder<String>();
        
        final Subscription subscription = 
            TestUtil.createObservableByHolder(unsubscribed1, holder1)
            .flatMap(TestUtil.flatMapFuncOf(unsubscribed2, holder2))
            .flatMap(TestUtil.flatMapFuncOf(unsubscribed3, holder3))
            .flatMap(TestUtil.flatMapFuncOf(unsubscribed4, holder4))
            .subscribe();
        
        assertEquals(1, holder1.getSubscriberCount());
        assertEquals(0, holder2.getSubscriberCount());
        assertEquals(0, holder3.getSubscriberCount());
        assertEquals(0, holder4.getSubscriberCount());
        
        holder1.getAt(0).onNext("hello");
        
        assertFalse(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        assertFalse(unsubscribed4.get());
        
        assertEquals(1, holder2.getSubscriberCount());
        assertEquals(0, holder3.getSubscriberCount());
        assertEquals(0, holder4.getSubscriberCount());
        holder2.getAt(0).onNext("world");
        
        assertFalse(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        assertFalse(unsubscribed4.get());
        
        assertEquals(1, holder3.getSubscriberCount());
        assertEquals(0, holder4.getSubscriberCount());
        holder3.getAt(0).onNext("rx");
        
        assertFalse(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        assertFalse(unsubscribed4.get());
        
        assertEquals(1, holder4.getSubscriberCount());
        holder4.getAt(0).onNext("java");
        
        assertFalse(unsubscribed1.get());
        assertFalse(unsubscribed2.get());
        assertFalse(unsubscribed3.get());
        assertFalse(unsubscribed4.get());
        
        subscription.unsubscribe();
        
        assertTrue(unsubscribed1.get());
        assertTrue(unsubscribed2.get());
        assertTrue(unsubscribed3.get());
        assertTrue(unsubscribed4.get());
    }

    @Test
    public final void testUnsubscribe() throws InterruptedException {
        final AtomicBoolean unsubscribed = new AtomicBoolean(false);
        final SubscriberHolder<String> holder = new SubscriberHolder<String>();
        
        final AtomicReference<Subscription> subscriptionRef = new AtomicReference<Subscription>();
        subscriptionRef.set(
            TestUtil.createObservableByHolder(unsubscribed, holder)
            .subscribe(new Observer<String>() {
                @Override
                public void onCompleted() {
                    
                }

                @Override
                public void onError(Throwable e) {
                    assertFalse(unsubscribed.get());
                    assertFalse(subscriptionRef.get().isUnsubscribed());
                    subscriptionRef.get().unsubscribe();
                    assertTrue(unsubscribed.get());
                    assertTrue(subscriptionRef.get().isUnsubscribed());
                }

                @Override
                public void onNext(String t) {
                    
                }}));
        
        assertEquals(1, holder.getSubscriberCount());
        
        holder.getAt(0).onError(new RuntimeException());
        
        assertTrue(unsubscribed.get());
    }

    @Test
    public final void testOnCompletedTiming() throws InterruptedException {
        final AtomicBoolean unsubscribed = new AtomicBoolean(false);
        final SubscriberHolder<String> holder = new SubscriberHolder<String>();
        
        final AtomicReference<Subscription> subscriptionRef = new AtomicReference<Subscription>();
        subscriptionRef.set(
            TestUtil.createObservableByHolder(unsubscribed, holder)
            .doOnTerminate(new Action0() {
                @Override
                public void call() {
                    System.out.println("doOnTerminate called.");
                }} )
            .doOnCompleted(new Action0() {
                @Override
                public void call() {
                    System.out.println("doOnCompleted called.");
                }})
            .subscribe(new Observer<String>() {
                @Override
                public void onCompleted() {
                    System.out.println("subscribe's onCompleted called.");
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("subscribe's onError called with " + e + ".");
                }

                @Override
                public void onNext(String t) {
                    
                }}));
        
        assertEquals(1, holder.getSubscriberCount());
        
        holder.getAt(0).onCompleted();
//        holder.getAt(0).onError(new RuntimeException("hello"));
    }

    @Test
    public final void testDoOnUnsubscribeCalledAfterOnCompleted() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean unsubscribeCalled = new AtomicBoolean(false);
        final AtomicBoolean onCompletedCalled = new AtomicBoolean(false);
        final SubscriberHolder<String> holder = new SubscriberHolder<String>();
        
        Observable.unsafeCreate(holder)
        .doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                unsubscribeCalled.set(true);
            }})
        .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                assertFalse(unsubscribeCalled.get());
                onCompletedCalled.set(true);
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String t) {
            }});
        
        assertFalse(unsubscribeCalled.get());
        holder.getAt(0).onCompleted();
        latch.await();
        assertTrue(unsubscribeCalled.get());
    }

    @Test
    public final void testDoOnUnsubscribeCalledAfterOnError() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean unsubscribeCalled = new AtomicBoolean(false);
        final AtomicBoolean onErrorCalled = new AtomicBoolean(false);
        final SubscriberHolder<String> holder = new SubscriberHolder<String>();
        
        Observable.unsafeCreate(holder)
        .doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                unsubscribeCalled.set(true);
            }})
        .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                assertFalse(unsubscribeCalled.get());
                onErrorCalled.set(true);
                latch.countDown();
            }

            @Override
            public void onNext(String t) {
            }});
        
        assertFalse(unsubscribeCalled.get());
        holder.getAt(0).onError(new RuntimeException());
        latch.await();
        assertTrue(unsubscribeCalled.get());
    }

    @Test
    public final void testDoOnUnsubscribeJustUnsubscribe() throws InterruptedException {
        final AtomicBoolean unsubscribeCalled = new AtomicBoolean(false);
        final SubscriberHolder<String> holder = new SubscriberHolder<String>();
        
        final Subscription subscription = Observable.unsafeCreate(holder)
        .doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                unsubscribeCalled.set(true);
            }})
        .subscribe();
        
        assertFalse(unsubscribeCalled.get());
        subscription.unsubscribe();
        assertTrue(unsubscribeCalled.get());
    }
}
