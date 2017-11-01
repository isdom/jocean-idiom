package org.jocean.idiom;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Beans {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(Beans.class);
    
    private Beans() {
        throw new IllegalStateException("No instances!");
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromString(final String value, final Class<T> type) {
        if (type.equals(String.class)) {
            return (T)value;
        } else {
            final PropertyEditor editor = PropertyEditorManager.findEditor(type);
            if (null != editor) {
                editor.setAsText(value);
                return (T)editor.getValue();
            } else {
                LOG.warn("can't found PropertyEditor for type({}), can't get bean from String({}).",
                        type, value);
                throw new RuntimeException(
                    "can't found PropertyEditor for type ("+ type +")");
            }
        }
    }
}
