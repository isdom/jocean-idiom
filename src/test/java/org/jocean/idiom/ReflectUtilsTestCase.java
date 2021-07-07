package org.jocean.idiom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

import org.junit.Test;

import rx.Observable;

public class ReflectUtilsTestCase {

	class Inner1 {
		class Inner2 {
		}

		Inner2 inner2 = new Inner2();
	};

	@Test
	public void testGetOuterFromInnerObjectForInner1() {
		final ReflectUtilsTestCase case1 = ReflectUtils.getOuterFromInnerObject(new Inner1());

		assertNotNull(case1);
	}

	@Test
	public void testGetOuterFromInnerObjectForInner2() {
		final Inner1 inner1 = new Inner1();
		final Inner1 innerRet = ReflectUtils.getOuterFromInnerObject(inner1.inner2);

		assertSame(innerRet, inner1);
	}

	static public class A implements Cloneable {

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + i;
            return result;
        }


        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final A other = (A) obj;
            if (i != other.i)
                return false;
            return true;
        }


        private final int i = 10;
	}

    @Test
    public void testClone() {
        final A a = new A();

        final A clonedA = ReflectUtils.invokeClone(a);

        assertEquals(a, clonedA);
    }

    public static class NonAnnotation {
        @SuppressWarnings("unused")
        private final String field1 = "test";
    }

    @Test
    public void testGetAnnotationFieldsOf() {
        final Field[] testFields = ReflectUtils.getAnnotationFieldsOf(NonAnnotation.class, Test.class);

        assertNotNull(testFields);
        assertEquals(0, testFields.length);
    }

    public static Observable<Integer> observableMethod1() {
        return null;
    }

    public static Observable observableMethod2() {
        return null;
    }

    public static Integer nonObservableMethod1() {
        return 1;
    }

    @Test
    public void testGetParameterizedRawType1() throws Exception {
        final Method method = ReflectUtilsTestCase.class.getDeclaredMethod("observableMethod1");
        final Class<?> rawType = ReflectUtils.getParameterizedRawType(method.getGenericReturnType());

        assertEquals(Observable.class, rawType);
    }

    @Test
    public void testGetParameterizedRawType2() throws Exception {
        final Method method = ReflectUtilsTestCase.class.getDeclaredMethod("nonObservableMethod1");
        final Class<?> rawType = ReflectUtils.getParameterizedRawType(method.getGenericReturnType());

        assertNull(rawType);
    }

    @Test
    public void testGetParameterizedRawType3() throws Exception {
        final Method method = ReflectUtilsTestCase.class.getDeclaredMethod("observableMethod2");
        final Class<?> rawType = ReflectUtils.getParameterizedRawType(method.getGenericReturnType());

        assertNull(rawType);
    }

    @Test
    public void testGetParameterizedTypeArgument1() throws Exception {
        final Method method = ReflectUtilsTestCase.class.getDeclaredMethod("observableMethod1");
        final Type typeArg0 = ReflectUtils.getParameterizedTypeArgument(method.getGenericReturnType(), 0);

        assertEquals(typeArg0, Integer.class);
    }

    Function<String, Map<String,String>>  functionField1;

    @Test
    public void testParameterizedTypedFieldCase1() throws Exception {
        final Field field = ReflectUtilsTestCase.class.getDeclaredField("functionField1");
        final Type typeArg1 = ReflectUtils.getParameterizedTypeArgument(field.getGenericType(), 1);

        assertTrue (typeArg1 instanceof ParameterizedType);

        assertEquals(Map.class, ((ParameterizedType)typeArg1).getRawType());
    }

    @SuppressWarnings("rawtypes")
    Function  classField1;

    @Test
    public void testGetRawType() throws Exception {
        final Field field1 = ReflectUtilsTestCase.class.getDeclaredField("functionField1");
        assertEquals(Function.class, ReflectUtils.getRawType(field1.getGenericType()));

        final Field field2 = ReflectUtilsTestCase.class.getDeclaredField("classField1");
        assertEquals(Function.class, ReflectUtils.getRawType(field2.getGenericType()));
    }
}
