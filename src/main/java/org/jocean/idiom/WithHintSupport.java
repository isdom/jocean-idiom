package org.jocean.idiom;

public class WithHintSupport implements WithHint {
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T hint() {
        return (T)this._hint;
    }
    
    @Override
    public <T> void setHint(final T hint) {
        this._hint = hint;
    }
    
    private Object _hint;
}
