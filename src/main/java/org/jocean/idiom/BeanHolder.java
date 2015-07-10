package org.jocean.idiom;


public interface BeanHolder {
    
    public <T> T getBean(final Class<T> requiredType);

}
