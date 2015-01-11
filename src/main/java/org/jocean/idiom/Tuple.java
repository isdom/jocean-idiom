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
        final Object[] newObjs = new Object[this._objs.length + objs.length];
        System.arraycopy(this._objs, 0, newObjs, 0, this._objs.length);
        System.arraycopy(objs, 0, newObjs, this._objs.length, objs.length);
        return new Tuple(newObjs);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAt(final int idx) {
        return (idx >=0 && idx < this._objs.length) ? (T)this._objs[idx] : null;
    }
    
    public <T> boolean instanceOf(final int idx, Class<T> cls) {
        final Object obj = getAt(idx);
        return null != obj ? cls.isAssignableFrom(obj.getClass()) : false;
    }
    
    public int size() {
        return this._objs.length;
    }
    
    private final Object[] _objs;
}
