package org.jocean.idiom;

import java.util.Comparator;

public interface Ordered {
    public int ordinal();
    
    public static final Comparator<Ordered> ASC = new Comparator<Ordered>() {
        @Override
        public int compare(final Ordered o1, final Ordered o2) {
            return o1.ordinal() - o2.ordinal();
        }
    };
    
    public static final Comparator<Ordered> DESC = new Comparator<Ordered>() {
        @Override
        public int compare(final Ordered o1, final Ordered o2) {
            return o2.ordinal() - o1.ordinal();
        }
    };
}
