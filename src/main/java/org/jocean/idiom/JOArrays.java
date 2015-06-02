package org.jocean.idiom;

import java.lang.reflect.Array;

public class JOArrays {
    private JOArrays() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> T[] addFirst(final Class<? extends T[]> arrayType, final T[] original, 
            @SuppressWarnings("unchecked") final T... toAdd) {
        final int toAddSize = (null != toAdd) ? toAdd.length : 0;
        final int newLength = (null!=original ? original.length : 0) + toAddSize;
        @SuppressWarnings("unchecked")
        final T[] copy = ((Object)arrayType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(arrayType.getComponentType(), newLength);
        if (toAddSize>0) {
            System.arraycopy(toAdd, 0, copy, 0, toAddSize);
        }
        if (null!=original) {
            System.arraycopy(original, 0, copy, toAddSize, original.length);
        }
        return copy;
    }
}
