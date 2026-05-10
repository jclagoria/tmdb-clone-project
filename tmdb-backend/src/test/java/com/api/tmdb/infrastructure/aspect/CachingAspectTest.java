package com.api.tmdb.infrastructure.aspect;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.infrastructure.annotation.Cacheable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingAspectTest {

    @Mock
    private CacheService cacheService;
    
    @Mock
    private ProceedingJoinPoint pjp;
    
    @Mock
    private MethodSignature methodSignature;
    
    private CachingAspect cachingAspect;

    @BeforeEach
    void setUp() {
        cachingAspect = new CachingAspect(cacheService);
    }

    @Test
    void cacheResult_shouldReturnCachedValue_whenCacheHit() throws Throwable {
        String cachedValue = "cachedResult";
        
        when(pjp.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(pjp.getTarget()).thenReturn(new TestService());
        
        Cacheable cacheable = mock(Cacheable.class);
        doReturn("'testKey'").when(cacheable).key();
        doReturn(String.class).when(cacheable).type();
        doReturn(30).when(cacheable).ttlMinutes();
        
        when(cacheService.get(anyString(), eq(String.class))).thenReturn(Mono.just(cachedValue));
        
        Object result = cachingAspect.cacheResult(pjp, cacheable);
        
        assertEquals(cachedValue, result);
        verify(pjp, never()).proceed();
        verify(cacheService, never()).set(any(), any(), any());
    }

    @Test
    void cacheResult_shouldExecuteMethodAndCache_whenCacheMiss() throws Throwable {
        String resultValue = "result";
        
        when(pjp.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(pjp.getTarget()).thenReturn(new TestService());
        when(pjp.getArgs()).thenReturn(new Object[]{});
        
        Cacheable cacheable = mock(Cacheable.class);
        doReturn("'testKey'").when(cacheable).key();
        doReturn(String.class).when(cacheable).type();
        doReturn(30).when(cacheable).ttlMinutes();
        
        when(cacheService.get(anyString(), eq(String.class))).thenReturn(Mono.empty());
        when(pjp.proceed()).thenReturn(resultValue);
        when(cacheService.set(anyString(), any(), any())).thenReturn(Mono.just(true));
        
        Object result = cachingAspect.cacheResult(pjp, cacheable);
        
        assertEquals(resultValue, result);
        verify(pjp).proceed();
        verify(cacheService).set(eq("testKey"), eq(resultValue), any(Duration.class));
    }

    @Test
    void cacheResult_shouldFallbackToDirectCall_whenCacheFails() throws Throwable {
        String resultValue = "result";
        
        when(pjp.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(pjp.getTarget()).thenReturn(new TestService());
        when(pjp.getArgs()).thenReturn(new Object[]{});
        
        Cacheable cacheable = mock(Cacheable.class);
        doReturn("'testKey'").when(cacheable).key();
        doReturn(String.class).when(cacheable).type();
        doReturn(30).when(cacheable).ttlMinutes();
        
        when(cacheService.get(anyString(), eq(String.class)))
            .thenReturn(Mono.error(new RuntimeException("Redis unavailable")));
        when(pjp.proceed()).thenReturn(resultValue);
        
        Object result = cachingAspect.cacheResult(pjp, cacheable);
        
        assertEquals(resultValue, result);
        verify(pjp).proceed();
        verify(cacheService, never()).set(any(), any(), any());
    }

    static class TestService {
        @Cacheable(key = "'testKey'", type = String.class, ttlMinutes = 30)
        public String testMethod() {
            return "result";
        }
    }
}