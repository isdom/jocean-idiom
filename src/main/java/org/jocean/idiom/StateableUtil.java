package org.jocean.idiom;

public class StateableUtil {
    private StateableUtil() {
        throw new IllegalStateException("No instances!");
    }
    
    public static <T> T stateOf(final Object obj) {
        return (obj instanceof Stateable) ? ((Stateable)obj).<T>state() : null;
    }
    
    public static <T> void setStateTo(final T state, final Object obj) {
        if (obj instanceof Stateable) {
            ((Stateable)obj).setState(state);
        }
    }

}
