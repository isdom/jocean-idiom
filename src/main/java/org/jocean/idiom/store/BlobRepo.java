package org.jocean.idiom.store;

import rx.Observable;

public interface BlobRepo {
    public interface Blob {
        public byte[] content();
        public String contentType();
    }
    
    public Observable<String> putBlob(
            final String key,
            final Blob blob);
    
    public Observable<Blob> getBlob(final String key);
}
