package org.jocean.idiom;

import java.lang.reflect.Method;

public interface Paired {
    public Class<?> pairedClass();
    public Method beginMethod();
    public Method endMethod();
}

