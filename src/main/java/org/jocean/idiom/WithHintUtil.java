package org.jocean.idiom;

public class WithHintUtil {
    private WithHintUtil() {
        throw new IllegalStateException("No instances!");
    }
    
    public static <T> T hintOf(final Object obj) {
        return (obj instanceof WithHint) ? ((WithHint)obj).<T>hint() : null;
    }
    
    public static <T> void setHintTo(final T hint, final Object obj) {
        if (obj instanceof WithHint) {
            ((WithHint)obj).setHint(hint);
        }
    }

}
