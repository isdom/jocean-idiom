package org.jocean.idiom;

public class StateableSupport implements Stateable {
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T state() {
        return (T)this._state;
    }
    
    @Override
    public <T> void setState(final T state) {
        this._state = state;
    }
    
    private Object _state;
}
