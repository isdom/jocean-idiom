package org.jocean.idiom;


public interface FetchAgent {
    
    public interface FetchReactor<CTX, VALUE> {
        
        public void onFetchResult(final CTX ctx, final VALUE value)
            throws Exception;
    }
    
    public <KEY, CTX, VALUE> Detachable fetchAsync(
            final KEY key, final CTX ctx, final FetchReactor<CTX, VALUE> reactor);
}
