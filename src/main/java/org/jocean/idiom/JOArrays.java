package org.jocean.idiom;

import java.lang.reflect.Array;

public class JOArrays {
    private JOArrays() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> T[] addFirst(final T[] original, final T toAdd, final Class<? extends T[]> arrayType) {
        final int newLength = (null!=original ? original.length : 0) + 1;
        @SuppressWarnings("unchecked")
        final T[] copy = ((Object)arrayType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(arrayType.getComponentType(), newLength);
        copy[0] = toAdd;
        if (null!=original) {
            System.arraycopy(original, 0, copy, 1,original.length);
        }
        return copy;
    }
}
