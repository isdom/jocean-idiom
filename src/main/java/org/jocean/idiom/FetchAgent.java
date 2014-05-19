package org.jocean.idiom;


public interface FetchAgent<KEY, VALUE> {
    
    public interface FetchReactor<CTX, VALUE> {
        
        public void onFetchResult(final CTX ctx, final VALUE value)
            throws Exception;
    }
    
    public <CTX> Detachable fetchAsync(
            final KEY key, final CTX ctx, final FetchReactor<CTX, VALUE> reactor);
}
