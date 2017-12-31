package org.jocean.idiom;

public interface WithHint {
    public <T> T hint();
    public <T> void setHint(final T hint);
}
