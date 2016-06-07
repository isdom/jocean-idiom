/**
 * 
 */
package org.jocean.idiom;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
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
	
    private static final Logger LOG = 
            LoggerFactory.getLogger(ReflectUtils.class);
    
    @SuppressWarnings("unchecked")
    public static <T> T invokeClone(final T cloneable) {
        try {
            final Method cloneMethod = cloneable.getClass().getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            return (T)cloneMethod.invoke(cloneable);
        } catch (Exception e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getOuterFromInnerObject(final Object inner) {
        try {
        	final Field[] fields = inner.getClass().getDeclaredFields();
        	for (Field field : fields) {
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
        } catch (Exception e) {
            LOG.warn("exception when getOuterFromInnerObject for inner {}, detail: {}",
                    inner, ExceptionUtils.exception2detail(e));
            return null;
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            LOG.error("exception when invoke enum({})'s static method values, detail:{}",
                    cls, ExceptionUtils.exception2detail(e));
            return (T[]) Array.newInstance(cls, 0);
        }
    }
    
    public static Class<?> getComponentClass(final Field field) {
        if ( null == field ) {
            String errmsg = "ReflectUtils: field is null, can't get compoment class.";
            LOG.error(errmsg);
            throw new RuntimeException(errmsg);
        }
        Type type = field.getGenericType();
        
        if ( null == type || !(type instanceof ParameterizedType) ) {
            String errmsg = "ReflectUtils: getGenericType invalid, can't get compoment class."
                +"/ cause field is [" + field + "]";
            LOG.error(errmsg);
            throw new RuntimeException(errmsg);
        }
        ParameterizedType parameterizedType  = (ParameterizedType)type;
        Class<?> clazz = (Class<?>)parameterizedType.getActualTypeArguments()[0];
        return  clazz;
    }
    
    public static Field[] getAllFieldsOfClass(final Class<?> cls) {
        Field[] fields = new Field[0];
        
        Class<?> itr = cls;
        while ( (null != itr) && !itr.equals(Object.class)) {
            fields = concat(itr.getDeclaredFields(), fields);
            itr = itr.getSuperclass();
        }
        
        return	fields;
    }
    
    public static Field[] getAnnotationFieldsOf(
	        final Class<?> cls, 
			final Class<? extends Annotation> annotationClass) {
        final List<Field> fs = new ArrayList<Field>();
        
        Class<?> itr = cls;
        while ( (null != itr) && !itr.equals(Object.class)) {
            for ( Field field : itr.getDeclaredFields() ) {
                if ( null != field.getAnnotation(annotationClass) ) {
                    field.setAccessible(true);
                    fs.add(field);
                }
            }
            itr = itr.getSuperclass();
        }
        
        return  fs.toArray(new Field[0]);
	}
	
	public static Method[]   getAnnotationMethodsOf(
            final Class<?> cls, 
            final Class<? extends Annotation> annotationClass) {
        final List<Method> ms = new ArrayList<Method>();
        
        Class<?> itr = cls;
        while ( (null != itr) && !itr.equals(Object.class)) {
            for ( Method method : itr.getDeclaredMethods()) {
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
        } catch (Exception e) {
            LOG.warn("exception when getDeclaredMethod for {}/{}, detail:{}",
                    cls, methodName, ExceptionUtils.exception2detail(e));
            return null;
        }
    }

    public static Method getMethodNamed(final Class<?> cls, final String methodName) {
        try {
            final Method[] methods = cls.getDeclaredMethods();
            for (Method m : methods) {
                if (m.getName().equals(methodName)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        } catch (Exception e) {
            LOG.warn("exception when getDeclaredMethod for {}/{}, detail:{}",
                    cls, methodName, ExceptionUtils.exception2detail(e));
        }
        return null;
    }
}
