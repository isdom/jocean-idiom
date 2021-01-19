/**
 *
 */
package org.jocean.idiom;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hp
 *
 */
public class ReflectUtils {

    private static final Field[] EMPTY_FIELDS = new Field[0];
    private static final Logger LOG = LoggerFactory.getLogger(ReflectUtils.class);

    @SuppressWarnings("unchecked")
    public static <T> T invokeClone(final T cloneable) {
        try {
            final Method cloneMethod = cloneable.getClass().getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            return (T)cloneMethod.invoke(cloneable);
        } catch (final Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOuterFromInnerObject(final Object inner) {
        try {
        	final Field[] fields = inner.getClass().getDeclaredFields();
        	for (final Field field : fields) {
        		//	"this$0" "this$1" "this$2" ...
        		//	http://nrg19840409.iteye.com/blog/1216036
        		/*
        		this$0就是内部类所自动保留的一个指向所在外部类的引用。
        		另外,受到$后的数字0启发,发现原来数字还可以有1,2,3..., 具体可以看如下代码就一清二楚了.
        		//Outer.java
        		public class Outer {//this$0
        		public class FirstInner {//this$1
        		  public class SecondInner {//this$2
        		   public class ThirdInner {
        		   }
        		  }
        		}
        		*/
        		//	so search for field named this$x
        		if (field.getName().startsWith("this$")) {
                    field.setAccessible(true);
                    return (T)field.get(inner);
        		}
        	}
        	return null;
//            final Field field = inner.getClass().getDeclaredField("this$0");
//            field.setAccessible(true);
//            return (T)field.get(inner);
        } catch (final Exception e) {
            LOG.warn("exception when getOuterFromInnerObject for inner {}, detail: {}",
                    inner, ExceptionUtils.exception2detail(e));
            return null;
        }
    }

    public static Method getStaticMethod(final String fullMethodName, final Class<?>... parameterTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException  {
        try {
            final int idx = fullMethodName.lastIndexOf('.');
            final String clsName = fullMethodName.substring(0, idx);
            final String methodName = fullMethodName.substring(idx+1);
            final Method method = Class.forName(clsName).getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
         } catch (final Exception e) {
            LOG.warn("exception when getStaticMethod for ({}), detail:{}",
                    fullMethodName, ExceptionUtils.exception2detail(e) );
            throw e;
        }
    }

    public static Method getStaticMethodByName(final String fullMethodName) throws ClassNotFoundException, NoSuchMethodException, SecurityException  {
        try {
            final int idx = fullMethodName.lastIndexOf('.');
            final String clsName = fullMethodName.substring(0, idx);
            final String methodName = fullMethodName.substring(idx+1);
            final Method[] methods = Class.forName(clsName).getDeclaredMethods();
            for (final Method m : methods) {
                if (m.getName().equals(methodName)) {
                    m.setAccessible(true);
                    return m;
                }
            }
            throw new NoSuchMethodException(fullMethodName);
         } catch (final Exception e) {
            LOG.warn("exception when getStaticMethodByName for ({}), detail:{}",
                    fullMethodName, ExceptionUtils.exception2detail(e) );
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStaticFieldValue(final String fullFieldName) {
        try {
            final int idx = fullFieldName.lastIndexOf('.');
            final String clsName = fullFieldName.substring(0, idx);
            final String fieldName = fullFieldName.substring(idx+1);
            final Class<?> cls = Class.forName(clsName);
            if ( null != cls ) {
                final Field field = cls.getDeclaredField(fieldName);
                if (null != field) {
                    field.setAccessible(true);
                    return (T)field.get(null);
                }
            }
        } catch (final Exception e) {
            LOG.warn("exception when getStaticFieldValue for ({}), detail:{}",
                    fullFieldName, ExceptionUtils.exception2detail(e) );
            throw new RuntimeException(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] getValuesOf(final Class<T> cls) {
        try {
            final Method valuesMethod = cls.getDeclaredMethod("values");
            valuesMethod.setAccessible(true);
            final T[] values = (T[])valuesMethod.invoke(null);
            return values;
        } catch (final Exception e) {
            LOG.error("exception when invoke enum({})'s static method values, detail:{}",
                    cls, ExceptionUtils.exception2detail(e));
            return (T[]) Array.newInstance(cls, 0);
        }
    }

    public static Class<?> getComponentClass(final Field field) {
        if ( null == field ) {
            final String errmsg = "ReflectUtils: field is null, can't get compoment class.";
            LOG.error(errmsg);
            throw new RuntimeException(errmsg);
        }
        final Type type = field.getGenericType();

        if ( null == type || !(type instanceof ParameterizedType) ) {
            final String errmsg = "ReflectUtils: getGenericType invalid, can't get compoment class."
                +"/ cause field is [" + field + "]";
            LOG.error(errmsg);
            throw new RuntimeException(errmsg);
        }
        final ParameterizedType parameterizedType  = (ParameterizedType)type;
        final Class<?> clazz = (Class<?>)parameterizedType.getActualTypeArguments()[0];
        return  clazz;
    }

    public static Field[] getAllFieldsOfClass(final Class<?> cls) {
        Field[] fields = EMPTY_FIELDS;

        Class<?> itr = cls;
        while ( (null != itr) && !itr.equals(Object.class)) {
            fields = concat(itr.getDeclaredFields(), fields);
            itr = itr.getSuperclass();
        }

        return	fields;
    }

    public static Field getFieldNamedDeep(final Class<?> cls, final String fieldName) {
        Class<?> itr = cls;
        while ( (null != itr) && !itr.equals(Object.class)) {
            try {
                final Field field = itr.getDeclaredField(fieldName);
                if (null != field) {
                    return field;
                }
            } catch (final Exception e) {
                // just ignore
            }
            itr = itr.getSuperclass();
        }

        return  null;
    }

    public static Field[] getAnnotationFieldsOf(
	        final Class<?> cls,
			final Class<? extends Annotation> annotationClass) {
        final List<Field> fs = new ArrayList<Field>();

        Class<?> itr = cls;
        while ( (null != itr) && !itr.equals(Object.class)) {
            for ( final Field field : itr.getDeclaredFields() ) {
                if ( null != field.getAnnotation(annotationClass) ) {
                    field.setAccessible(true);
                    fs.add(field);
                }
            }
            itr = itr.getSuperclass();
        }

        return  fs.toArray(EMPTY_FIELDS);
	}

	public static Method[]   getAnnotationMethodsOf(
            final Class<?> cls,
            final Class<? extends Annotation> annotationClass) {
        final List<Method> ms = new ArrayList<Method>();

        Class<?> itr = cls;
        while ( (null != itr) && !itr.equals(Object.class)) {
            for ( final Method method : itr.getDeclaredMethods()) {
                if ( null != method.getAnnotation(annotationClass) ) {
                    method.setAccessible(true);
                    ms.add(method);
                }
            }
            itr = itr.getSuperclass();
        }

        return  ms.toArray(new Method[0]);
    }

	private static <T> T[] concat(final T[] first, final T[] second) {
	    if ( null == first && null == second ) {
	        return null;
	    }

        if ( null == second ) {
            return Arrays.copyOf(first, first.length);
        }

        if ( null == first ) {
            return Arrays.copyOf(second, second.length);
        }

	    final int totalLength = first.length + second.length;

	    final T[] result =
	            Arrays.copyOf(first, totalLength);
	    System.arraycopy(second, 0, result, first.length, second.length);
	    return result;
	}

    public static Method getMethodOf(final Class<?> cls, final String methodName, final Class<?> ...parameterTypes) {
        try {
            final Method method = cls.getDeclaredMethod(methodName, parameterTypes);
            if (null!=method) {
                method.setAccessible(true);
            }
            return method;
        } catch (final Exception e) {
            LOG.warn("exception when getDeclaredMethod for {}/{}, detail:{}",
                    cls, methodName, ExceptionUtils.exception2detail(e));
            return null;
        }
    }

    public static Method getMethodNamed(final Class<?> cls, final String methodName) {
        try {
            final Method[] methods = cls.getDeclaredMethods();
            for (final Method m : methods) {
                if (m.getName().equals(methodName)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        } catch (final Exception e) {
            LOG.warn("exception when getDeclaredMethod for {}/{}, detail:{}",
                    cls, methodName, ExceptionUtils.exception2detail(e));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(final Class<T> type) {
        final Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (final Constructor<?> c : constructors) {
            if (c.getParameterTypes().length == 0) {
                c.setAccessible(true);
                try {
                    return (T) c.newInstance();
                } catch (final Exception e) {
                    LOG.warn("exception when invoke ({}).newInstance, detail: {}", c, ExceptionUtils.exception2detail(e));
                }
            }
        }
        return null;
    }

    public static Class<?> getParameterizedRawType(final Type genericType) {
        if (genericType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)genericType;
            return (Class<?>)parameterizedType.getRawType();
        } else {
            return null;
        }
    }

    public static Type getParameterizedTypeArgument(final Type genericType, final int idx) {
        if (genericType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)genericType;
            final Type[] typeArguments = parameterizedType.getActualTypeArguments();
            return null != typeArguments && typeArguments.length > idx ? typeArguments[idx] : null;
        } else {
            return null;
        }
    }
}
