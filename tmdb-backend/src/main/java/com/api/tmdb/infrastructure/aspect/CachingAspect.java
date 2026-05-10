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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Method;
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
        log.info("[AOP_CACHE] CachingAspect initialized!");
    }

    @Around("@annotation(com.api.tmdb.infrastructure.annotation.Cacheable)")
    public Object cacheResult(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().getSimpleName();
        
        log.debug("[AOP_CACHE] Intercepting method: {}.{}()", className, methodName);
        
        String cacheKey = buildCacheKey(pjp, cacheable.key());
        Class<?> returnType = cacheable.type();
        int ttlMinutes = cacheable.ttlMinutes();
        
        log.debug("[AOP_CACHE] Generated key: {}, TTL: {} minutes, type: {}", cacheKey, ttlMinutes, returnType.getName());
        
        Mono<Object> cachingMono = Mono.fromCallable(() -> {
            try {
                Object cached = cacheService.get(cacheKey, returnType).block();
                
                if (cached != null) {
                    log.info("[AOP_CACHE_HIT] {}.{}() - key: {}", className, methodName, cacheKey);
                    return cached;
                }
                
                log.debug("[AOP_CACHE_MISS] {}.{}() - key: {}", className, methodName, cacheKey);
                
                Object result;
                try {
                    result = pjp.proceed();
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
                
                if (result instanceof Mono<?> mono) {
                    Object resolvedValue = mono.block();
                    
                    if (resolvedValue != null) {
                        cacheService.set(cacheKey, resolvedValue, Duration.ofMinutes(ttlMinutes)).block();
                        log.info("[AOP_CACHE_SET] {}.{}() - key: {}, TTL: {}min", 
                                 className, methodName, cacheKey, ttlMinutes);
                    }
                    
                    return resolvedValue;
                }
                
                if (result != null) {
                    cacheService.set(cacheKey, result, Duration.ofMinutes(ttlMinutes)).block();
                    log.info("[AOP_CACHE_SET] {}.{}() - key: {}, TTL: {}min", 
                             className, methodName, cacheKey, ttlMinutes);
                }
                
                return result;
            } catch (Exception e) {
                log.error("[AOP_CACHE_ERROR] {}.{}() - key: {}", className, methodName, cacheKey);
                throw e;
            }
        })
        .onErrorResume(e -> {
            log.warn("[AOP_CACHE] Cache failed, falling back to direct call: {}", e.getMessage());
            try {
                Object result = pjp.proceed();
                if (result instanceof Mono<?> mono) {
                    return mono;
                }
                return Mono.just(result);
            } catch (Throwable t) {
                log.error("[AOP_CACHE_ERROR] {}.{}() - key: {}", className, methodName, cacheKey);
                return Mono.error(t);
            }
        })
        .subscribeOn(Schedulers.boundedElastic());
        
        return cachingMono;
    }

    private String buildCacheKey(ProceedingJoinPoint pjp, String keyExpression) {
        if (keyExpression == null || keyExpression.isEmpty()) {
            log.info("[AOP_CACHE] Empty key expression, using default");
            return generateDefaultKey(pjp);
        }
        
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] arguments = pjp.getArgs();
            
            log.debug("[AOP_CACHE] Parameter names: {}, Arguments: {}",
                     parameterNames != null ? Arrays.toString(parameterNames) : "null",
                     arguments != null ? Arrays.toString(arguments) : "null");
            
            if (parameterNames != null && arguments != null && parameterNames.length == arguments.length) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], arguments[i]);
                }
            } else if (arguments != null && arguments.length > 0) {
                for (int i = 0; i < arguments.length; i++) {
                    context.setVariable("a" + i, arguments[i]);
                }
            }
            
            String evalExpression = keyExpression;
            if ((parameterNames == null || parameterNames.length == 0) && arguments != null && arguments.length > 0) {
                evalExpression = buildSimpleKeyExpression(arguments);
                log.info("[AOP_CACHE] Using fallback key expression: {}", evalExpression);
            }
            
            Object result = parser.parseExpression(evalExpression).getValue(context);
            String finalKey = result != null ? result.toString() : generateDefaultKey(pjp);
            log.info("[AOP_CACHE] SpEL result key: {}", finalKey);
            return finalKey;
            
        } catch (Exception e) {
            log.error("[AOP_CACHE] Failed to evaluate key expression: {}, using default. Error: {}",
                     keyExpression, e.getMessage());
            return generateDefaultKey(pjp);
        }
    }
    
    private String buildSimpleKeyExpression(Object[] arguments) {
        StringBuilder sb = new StringBuilder("'key:'");
        for (int i = 0; i < arguments.length; i++) {
            sb.append(" + #a").append(i);
            if (i < arguments.length - 1) {
                sb.append(" + ':' + ");
            }
        }
        return sb.toString();
    }

    private String generateDefaultKey(ProceedingJoinPoint pjp) {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String argsHash = Arrays.hashCode(pjp.getArgs()) + "";
        
        return className + "." + methodName + ":" + argsHash;
    }
}