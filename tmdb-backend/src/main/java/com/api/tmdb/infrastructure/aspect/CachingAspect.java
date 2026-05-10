package com.api.tmdb.infrastructure.aspect;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.infrastructure.annotation.Cacheable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Aspect
@Component
public class CachingAspect {

    private static final Logger log = LoggerFactory.getLogger(CachingAspect.class);
    
    private final CacheService cacheService;
    private final ExpressionParser parser = new SpelExpressionParser();

    public CachingAspect(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Around("@annotation(Cacheable)")
    public Object cacheResult(ProceedingJoinPoint pjp, Cacheable cacheable) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().getSimpleName();
        
        log.debug("[AOP_CACHE] Intercepting method: {}.{}()", className, methodName);
        
        String cacheKey = buildCacheKey(pjp, cacheable.key());
        Class<?> returnType = cacheable.type();
        int ttlMinutes = cacheable.ttlMinutes();
        
        log.debug("[AOP_CACHE] Generated key: {}, TTL: {} minutes", cacheKey, ttlMinutes);
        
        try {
            Object cached = cacheService.get(cacheKey, returnType).block();
            
            if (cached != null) {
                log.info("[AOP_CACHE_HIT] {}.{}() - key: {}", className, methodName, cacheKey);
                return cached;
            }
            
            log.debug("[AOP_CACHE_MISS] {}.{}() - key: {}", className, methodName, cacheKey);
            
            Object result = pjp.proceed();
            
            if (result != null) {
                cacheService.set(cacheKey, result, Duration.ofMinutes(ttlMinutes)).block();
                log.info("[AOP_CACHE_SET] {}.{}() - key: {}, TTL: {}min", 
                         className, methodName, cacheKey, ttlMinutes);
            }
            
            return result;
            
        } catch (Exception e) {
            log.warn("[AOP_CACHE] Cache failed, falling back to direct call: {}", e.getMessage());
            return pjp.proceed();
        }
    }

    private String buildCacheKey(ProceedingJoinPoint pjp, String keyExpression) {
        if (keyExpression == null || keyExpression.isEmpty()) {
            return generateDefaultKey(pjp);
        }
        
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] arguments = pjp.getArgs();
            
            if (parameterNames != null && arguments != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], arguments[i]);
                }
            }
            
            Object result = parser.parseExpression(keyExpression).getValue(context);
            return result != null ? result.toString() : generateDefaultKey(pjp);
            
        } catch (Exception e) {
            log.warn("[AOP_CACHE] Failed to evaluate key expression: {}, using default", 
                     keyExpression, e);
            return generateDefaultKey(pjp);
        }
    }

    private String generateDefaultKey(ProceedingJoinPoint pjp) {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String argsHash = Arrays.hashCode(pjp.getArgs()) + "";
        
        return className + "." + methodName + ":" + argsHash;
    }
}