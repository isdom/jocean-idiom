package org.jocean.idiom;


public interface BeanHolder {

    public <T> T getBean(final Class<T> requiredType);

    public <T> T getBean(final String name, final Class<T> requiredType);

    public Object getBean(final String name);

    public Object getBean(final String name, final Object... args);

    public <T> T getBean(final Class<T> requiredType, final Object... args);
}
