package org.jocean.idiom;

import java.io.Serializable;
import java.util.Arrays;

public class Tuple implements Serializable {
    
    private static final long serialVersionUID = 596776401031096185L;

    @Override
    public String toString() {
        return "Tuple" + Arrays.toString(_objs);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this._objs);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple other = (Tuple) obj;
        if (!Arrays.equals(_objs, other._objs))
            return false;
        return true;
    }

    public static Tuple of(final Object ...objs) {
        return new Tuple(objs);
    }
    
    private Tuple(final Object ...objs) {
        this._objs = objs;
    }
    
    public Tuple append(final Object ...objs) {
        if ( null == objs || objs.length == 0) {
            return this;
        }
        final Object[] newObjs = new Object[size() + objs.length];
        if ( size() > 0 ) {
            System.arraycopy(this._objs, 0, newObjs, 0, size());
        }
        System.arraycopy(objs, 0, newObjs, size(), objs.length);
        return new Tuple(newObjs);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAt(final int idx) {
        return (idx >=0 && idx < size()) ? (T)this._objs[idx] : null;
    }
    
    public <T> boolean instanceOf(final int idx, Class<T> cls) {
        final Object obj = getAt(idx);
        return null != obj ? cls.isAssignableFrom(obj.getClass()) : false;
    }
    
    public int size() {
        return null != this._objs ? this._objs.length : 0;
    }
    
    private final Object[] _objs;
}
