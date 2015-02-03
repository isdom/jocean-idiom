package org.jocean.idiom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface PairedVisitor<T> {
    public void visitBegin(final T obj);
    public void visitEnd(final T obj);

    public static class Utils {
        private static final Logger LOG = 
                LoggerFactory.getLogger(Utils.class);
        
        @SafeVarargs
        public static <T> PairedVisitor<T> composite(final PairedVisitor<T> ...paireds) {
            return new PairedVisitor<T>() {

                @Override
                public void visitBegin(final T obj) {
                    for ( PairedVisitor<T> paired : paireds ) {
                        try {
                            paired.visitBegin(obj);
                        }
                        catch (Throwable e) {
                            LOG.error("exception when invoke PairedVisitor({})'s visitBegin for {}, detail:{}",
                                    paired, obj, ExceptionUtils.exception2detail(e));
                        }
                    }
                }

                @Override
                public void visitEnd(final T obj) {
                    for ( PairedVisitor<T> paired : paireds ) {
                        try {
                            paired.visitEnd(obj);
                        }
                        catch (Throwable e) {
                            LOG.error("exception when invoke PairedVisitor({})'s visitEnd for {}, detail:{}",
                                    paired, obj, ExceptionUtils.exception2detail(e));
                        }
                    }
                }};
        }
    }
}
