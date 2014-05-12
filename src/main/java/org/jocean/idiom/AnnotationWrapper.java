/**
 * 
 */
package org.jocean.idiom;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author isdom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationWrapper {
    abstract Class<?> value();
}
