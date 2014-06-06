/**
 * 
 */
package org.jocean.idiom;


/**
 * @author isdom
 *
 */
public class ValidationId {

    public boolean isValidId(final int id) {
        return (id == this._currentId);
    }
    
    public int updateIdAndGet() {
        return ++this._currentId;
    }
    
    @Override
    public String toString() {
        return "ValidationId [_currentId=" + _currentId + "]";
    }

    private int _currentId = 0;
}
