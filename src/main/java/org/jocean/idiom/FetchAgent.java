package org.jocean.idiom;


public interface FetchAgent<KEY, CTX, VALUE> {
    
    public interface FetchReactor<CTX, VALUE> {
        public void onFetchComplete(final CTX ctx, final VALUE value)
            throws Exception;
    }
    
    public Detachable fetchAsync(
            final KEY key, final CTX ctx, final FetchReactor<CTX, VALUE> reactor);
}
