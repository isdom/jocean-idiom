package org.jocean.idiom.rx;

import rx.Subscriber;

final class GuardUnsubscribedSubscriber<T> extends Subscriber<T> {

    private final Subscriber<? super T> actual;

    public GuardUnsubscribedSubscriber(Subscriber<? super T> actual) {
        super(actual);
        this.actual = actual;
    }

    /**
     * Notifies the Subscriber that the {@code Observable} has finished sending push-based notifications.
     * <p>
     * The {@code Observable} will not call this method if it calls {@link #onError}.
     */
    @Override
    public void onCompleted() {
        if (!isUnsubscribed()) {
            actual.onCompleted();
        }
    }

    /**
     * Notifies the Subscriber that the {@code Observable} has experienced an error condition.
     * <p>
     * If the {@code Observable} calls this method, it will not thereafter call {@link #onNext} or
     * {@link #onCompleted}.
     * 
     * @param e
     *          the exception encountered by the Observable
     */
    @Override
    public void onError(final Throwable e) {
        if (!isUnsubscribed()) {
            actual.onError(e);
        }
    }

    /**
     * Provides the Subscriber with a new item to observe.
     * <p>
     * The {@code Observable} may call this method 0 or more times.
     * <p>
     * The {@code Observable} will not call this method again after it calls either {@link #onCompleted} or
     * {@link #onError}.
     * 
     * @param args
     *          the item emitted by the Observable
     */
    @Override
    public void onNext(final T args) {
        if (!isUnsubscribed()) {
            actual.onNext(args);
        }
    }
    
    /**
     * Returns the {@link Subscriber} underlying this {@code SafeSubscriber}.
     *
     * @return the {@link Subscriber} that was used to create this {@code SafeSubscriber}
     */
    public Subscriber<? super T> getActual() {
        return actual;
    }

    @Override
    public String toString() {
        return actual.toString();
    }
}
