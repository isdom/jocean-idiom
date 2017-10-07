package org.jocean.idiom;

public interface DisposableWrapper<E> {
    
    public E unwrap();
    
    public void dispose();
    
    public boolean isDisposed();
}
