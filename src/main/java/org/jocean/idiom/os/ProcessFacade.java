package org.jocean.idiom.os;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jocean.idiom.io.LineBuffer;

import rx.functions.Action0;
import rx.functions.Action1;

public interface ProcessFacade {
    public void shutdown();

    public boolean readStdout(final Action1<String> online);
    
    // TODO, add onEnded callback 
    // public void addOnEnd(final Action0 onEnd);

    public static class Util {
        public static ProcessFacade wrapProcess(final Process p, final Action0 onEnd) {
            final Reader in = wrapInReader(p);
            final char[] cbuf = new char[256];
            final AtomicReference<Action1<String>> actionRef = new AtomicReference<>();
            final LineBuffer lineBuf = new LineBuffer() {
                @Override
                protected void handleLine(final String line, final String end) throws IOException {
                    try {
                        final Action1<String> action = actionRef.get();
                        if (null != action) {
                            action.call(line);
                        }
                    } catch (Exception e) {
                    }
                }
            };

            final ProcessFacade facade = new ProcessFacade() {
                @Override
                public void shutdown() {
                    p.destroyForcibly();
                }

                @Override
                public boolean readStdout(final Action1<String> online) {
                    // set current action
                    actionRef.set(online);
                    try {
                        while (p.isAlive() && in.ready()) {
                            final int readcnt = in.read(cbuf);
                            if (readcnt > 0) {
                                lineBuf.add(cbuf, 0, readcnt);
                            }
                        }
                        if (!p.isAlive()) {
                            cleanup(lineBuf, onEnd);
                            throwOnError(p);
                            // true means process ended
                            return true;
                        }
                        // false means process NOT ended
                        return false;
                    } catch (Exception e) {
                        cleanup(lineBuf, onEnd);
                        p.destroy();
                        throw new RuntimeException(e);
                    } finally {
                        actionRef.set(null);
                    }
                }
            };
            return facade;
        }

        private static Reader wrapInReader(Process p) {
            return new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8);
        }

        private static void throwOnError(Process p) throws IOException {
            try {
                if (!p.waitFor(1, TimeUnit.SECONDS)) {
                    // TODO Parse the error
                    throw new IOException(p + " returned non-zero exit status. Check stdout.");
                }
            } catch (InterruptedException e) {
                throw new IOException("InterruptedException waiting for " + p + " to finish.");
            }
        }

        private static void cleanup(final LineBuffer lineBuf, final Action0 onEnd) {
            try {
                lineBuf.finish();
            } catch (IOException e) {
            }
            if (null != onEnd) {
                onEnd.call();
            }
        }
    }
}
