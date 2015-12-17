package org.jocean.idiom;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeOp {
    private static Unsafe getUnsafe() {
        try {
            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    private static long normalize(final int value) {
        return (value >= 0) ? value : (~0L >>> 32) & value;
    }
    
    public static long toAddress(final Object obj) {
        Object[] array = new Object[] {obj};
        long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
        return normalize(getUnsafe().getInt(array, baseOffset));
    }

    public static Object fromAddress(final long address) {
        Object[] array = new Object[] {null};
        long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
        getUnsafe().putLong(array, baseOffset, address);
        return array[0];
    }
}
