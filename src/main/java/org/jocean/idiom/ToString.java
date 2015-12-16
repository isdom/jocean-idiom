package org.jocean.idiom;

import java.util.Collection;

public class ToString {
    public static <E> String toMultiline(final Collection<E> objs) {
        final StringBuilder sb = new StringBuilder();
        String splitter = "";
        sb.append('[');
        for (E e : objs) {
            sb.append(splitter);
            sb.append(e.toString());
            splitter = ",\r\n";
        }
        sb.append(']');
        return sb.toString();
    }
}
