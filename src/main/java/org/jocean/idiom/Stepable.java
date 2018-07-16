package org.jocean.idiom;

public interface Stepable<E> {
    public void step();

    public E element();
}
