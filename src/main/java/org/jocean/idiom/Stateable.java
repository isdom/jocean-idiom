package org.jocean.idiom;

public interface Stateable {
    public <T> T state();
    public <T> void setState(final T state);
}
