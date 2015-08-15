package org.jocean.idiom;


public interface BeanHolder {
    
    public <T> T getBean(final Class<T> requiredType);

    public <T> T getBean(final String name, final Class<T> requiredType);
}
