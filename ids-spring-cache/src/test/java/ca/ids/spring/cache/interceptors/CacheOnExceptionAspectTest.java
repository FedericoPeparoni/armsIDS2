package ca.ids.spring.cache.interceptors;

import ca.ids.spring.cache.CacheableException;
import ca.ids.spring.cache.CacheableExceptionProvider;
import ca.ids.spring.cache.annotations.CacheableOnException;
import ca.ids.spring.cache.exceptions.CacheRetryException;
import ca.ids.spring.cache.exceptions.CacheableRuntimeException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CacheOnExceptionAspectTest {

    private CacheManager cacheManager;
    private KeyGenerator keyGenerator;
    private CacheableExceptionProvider cacheableExceptionProvider;
    private CacheOnExceptionAspect cacheOnExceptionAspect;

    private CacheableOnException cacheableOnException;
    private MethodSignature methodSignature;
    private CacheOnExceptionAspectMock object;
    private ProceedingJoinPoint pjp;

    @Before
    public void setup() throws Throwable {

        object = new CacheOnExceptionAspectMock();

        cacheManager = mock(CacheManager.class);
        keyGenerator = mock(KeyGenerator.class);
        cacheableExceptionProvider = mock(CacheableExceptionProvider.class);
        cacheOnExceptionAspect = new CacheOnExceptionAspect(cacheManager, keyGenerator, cacheableExceptionProvider);

        cacheableOnException = mock(CacheableOnException.class);
        methodSignature = mock(MethodSignature.class);
        pjp = mock(ProceedingJoinPoint.class);

        Cache cache = mock(Cache.class);
        Cache.ValueWrapper cacheValueWrapper = mock(Cache.ValueWrapper.class);
        SimpleKey simpleKey = mock(SimpleKey.class);

        when(cacheManager.getCache(anyString())).thenReturn(cache);

        when(keyGenerator.generate(anyObject(), any(), any())).thenReturn(simpleKey);

        when(cacheableExceptionProvider.handleException(any(), any(Throwable.class), anyBoolean())).thenReturn(true);

        when(cacheableOnException.exceptions()).thenReturn(new Class[] { Throwable.class });
        when(cacheableOnException.retry()).thenReturn(true);
        when(cacheableOnException.value()).thenReturn(new String[] { CacheOnExceptionAspectMock.CACHE_NAME });

        when(methodSignature.getMethod()).thenReturn(object.getClass()
            .getMethod(CacheOnExceptionAspectMock.METHOD_NAME, CacheOnExceptionAspectMock.PARAM_TYPE));
        when(methodSignature.getReturnType()).thenReturn(CacheOnExceptionAspectMock.RETURN_TYPE);

        when(pjp.getTarget()).thenReturn(object);
        when(pjp.getThis()).thenReturn(object);
        when(pjp.proceed()).thenReturn(CacheOnExceptionAspectMock.RESULT);
        when(pjp.getSignature()).thenReturn(methodSignature);
        when(pjp.getArgs()).thenReturn(new Object[] { CacheOnExceptionAspectMock.ARG });

        when(cache.get(anyObject())).thenReturn(cacheValueWrapper);

        when(cacheValueWrapper.get()).thenReturn(CacheOnExceptionAspectMock.RESULT);
    }

    @Test
    public void cacheableOnExceptionAnnotationTest() throws Throwable {

        // run public method for annotated methods
        Object object = cacheOnExceptionAspect.adviseAnnotatedMethods(pjp, cacheableOnException);

        // perform necessary checks
        assertThat(object).isEqualTo(CacheOnExceptionAspectMock.RESULT);

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(1)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(1)).generate(anyObject(), any(), any());
    }

    @Test
    public void cacheOnExceptionAnnotationTest() throws Throwable {

        // force interceptor to use class config annotation
        when(cacheableOnException.exceptions()).thenReturn(new Class[] {});
        when(cacheableOnException.value()).thenReturn(new String[] {});

        // run public method for methods of annotated classes
        Object object = cacheOnExceptionAspect.adviseMethodsOfAnnotatedClass(pjp, cacheableOnException);

        // perform necessary checks
        assertThat(object).isEqualTo(CacheOnExceptionAspectMock.RESULT);

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(1)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(1)).generate(anyObject(), any(), any());

    }

    @Test
    public void methodThrowTest() throws Throwable {

        // define class method params method results
        when(pjp.proceed()).thenThrow(new Exception(new Throwable()));

        // run public method for methods of annotated classes
        try {
            cacheOnExceptionAspect.adviseMethodsOfAnnotatedClass(pjp, cacheableOnException);
            fail("Expected " + CacheRetryException.class + " thrown when return method is not " + void.class + ".");
        } catch (CacheRetryException ignored) {}

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(0)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(0)).generate(anyObject(), any(), any());
        verify(cacheableExceptionProvider, times(1)).create(any(CacheableException.class));
    }

    @Test
    public void voidMethodThrowTest() throws Throwable {

        // define class method params method results
        when(pjp.proceed()).thenThrow(new CacheableRuntimeException(new String[] {
            "INSERT INTO mock.resource (mock_column) VALUES ('mock_value')"
        }, new Throwable()));

        // define method with return type of void
        when(methodSignature.getMethod()).thenReturn(object.getClass()
            .getMethod(CacheOnExceptionAspectMock.METHOD_VOID_NAME, CacheOnExceptionAspectMock.PARAM_TYPE));
        when(methodSignature.getReturnType()).thenReturn(void.class);

        // run public method for methods of annotated classes
        Object object = cacheOnExceptionAspect.adviseMethodsOfAnnotatedClass(pjp, cacheableOnException);

        // perform necessary checks
        assertThat(object).isEqualTo(null);

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(0)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(0)).generate(anyObject(), any(), any());
        verify(cacheableExceptionProvider, times(1)).create(any(CacheableException.class));
    }

    @Test
    public void noRetryTest() throws Throwable {

        // force interceptor to use class config annotation
        when(cacheableOnException.retry()).thenReturn(false);

        // define class method params method results
        when(pjp.proceed()).thenThrow(new Exception(new Throwable()));

        // run public method for annotated methods
        Object object = cacheOnExceptionAspect.adviseAnnotatedMethods(pjp, cacheableOnException);

        // perform necessary checks
        assertThat(object).isEqualTo(CacheOnExceptionAspectMock.RESULT);
    }

    @Test
    public void unhandledTest() throws Throwable {

        // define class method params method results
        when(pjp.proceed()).thenThrow(new Exception());

        // define exception handler results, false to skip
        when(cacheableExceptionProvider.handleException(any(), any(Throwable.class), anyBoolean())).thenReturn(false);

        // run public method for methods of annotated classes
        try {
            cacheOnExceptionAspect.adviseMethodsOfAnnotatedClass(pjp, cacheableOnException);
            fail("Expected " + Exception.class + " thrown when not included in annotation exception parameter.");
        } catch (Exception ignored) {}

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(0)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(0)).generate(anyObject(), any(), any());
        verify(cacheableExceptionProvider, times(0)).create(any(CacheableException.class));
    }

    @Test
    public void cacheAllTest() throws Throwable {

        // replace all object references to class without config annotation
        object = new CacheOnExceptionAspectAllMock();
        when(methodSignature.getMethod()).thenReturn(object.getClass()
            .getMethod(CacheOnExceptionAspectMock.METHOD_NAME, CacheOnExceptionAspectMock.PARAM_TYPE));
        when(pjp.getTarget()).thenReturn(object);
        when(pjp.getThis()).thenReturn(object);

        // force interceptor to use class config annotation
        when(cacheableOnException.exceptions()).thenReturn(new Class[] {});
        when(cacheableOnException.value()).thenReturn(new String[] {});

        // define class method params method results
        when(pjp.proceed()).thenThrow(new Throwable());

        // run public method for methods of annotated classes
        try {
            cacheOnExceptionAspect.adviseMethodsOfAnnotatedClass(pjp, cacheableOnException);
            fail("Expected " + CacheRetryException.class + " thrown when annotated exception parameter value is empty.");
        } catch (CacheRetryException ignored) {}

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(0)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(0)).generate(anyObject(), any(), any());
        verify(cacheableExceptionProvider, times(1)).create(any(CacheableException.class));
    }

    @Test
    public void illegalStateTest() throws Throwable {

        // replace all object references to class without config annotation
        object = new CacheOnExceptionAspectAllMock();
        when(methodSignature.getMethod()).thenReturn(object.getClass()
            .getMethod(CacheOnExceptionAspectMock.METHOD_NAME, CacheOnExceptionAspectMock.PARAM_TYPE));
        when(pjp.getTarget()).thenReturn(object);
        when(pjp.getThis()).thenReturn(object);

        // force interceptor to use class config annotation
        when(cacheableOnException.exceptions()).thenReturn(new Class[] {});
        when(cacheableOnException.retry()).thenReturn(false);
        when(cacheableOnException.value()).thenReturn(new String[] {});

        // define class method params method results
        when(pjp.proceed()).thenThrow(new Throwable());

        // run public method for methods of annotated classes
        try {
            cacheOnExceptionAspect.adviseMethodsOfAnnotatedClass(pjp, cacheableOnException);
            fail("Expected " + IllegalStateException.class + " thrown when retry false and no annotated cache store value.");
        } catch (Throwable ignored) {}

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(0)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(1)).generate(anyObject(), any(), any());
        verify(cacheableExceptionProvider, times(0)).create(any(CacheableException.class));

    }

    @Test
    public void pointcutTest() {
        cacheOnExceptionAspect.annotatedMethod();
        cacheOnExceptionAspect.methodOfAnnotatedClass();

        // verify that necessary dependency injection methods have been run
        verify(cacheManager, times(0)).getCache(anyString());
        verify(keyGenerator, times(0)).generate(anyObject(), any());
        verify(keyGenerator, times(0)).generate(anyObject(), any(), any());
        verify(cacheableExceptionProvider, times(0)).create(any(CacheableException.class));
    }
}
