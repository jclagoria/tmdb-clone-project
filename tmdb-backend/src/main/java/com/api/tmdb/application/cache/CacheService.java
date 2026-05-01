package com.api.tmdb.application.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public CacheService(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> Mono<T> get(String key, Class<T> type) {
        log.debug("[CACHE_GET] Attempting to get key: {}", key);
        return redisTemplate.opsForValue()
                .get(key)
                .cast(type)
                .doOnNext(value -> log.debug("[CACHE_HIT] Found in cache key: {}", key))
                .doOnError(error -> log.error("[CACHE_ERROR] Get failed for key: {}, error: {}", key, error.getMessage()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("[CACHE_MISS] Key not found in cache: {}", key);
                    return Mono.empty();
                }));
    }

    public Mono<Boolean> set(String key, Object value, Duration ttl) {
        log.debug("[CACHE_SET] Storing in cache key: {}, ttl: {} seconds", key, ttl.getSeconds());
        return redisTemplate.opsForValue()
                .set(key, value, ttl)
                .doOnNext(result -> log.debug("[CACHE_SET_SUCCESS] Key stored: {}, result: {}", key, result))
                .doOnError(error -> log.error("[CACHE_ERROR] Set failed for key: {}, error: {}", key, error.getMessage()));
    }

    public Mono<Boolean> delete(String key) {
        log.debug("[CACHE_DELETE] Deleting key: {}", key);
        return redisTemplate.delete(key)
                .thenReturn(true)
                .doOnNext(result -> log.debug("[CACHE_DELETE_SUCCESS] Key deleted: {}, result: {}", key, result))
                .doOnError(error -> log.error("[CACHE_ERROR] Delete failed for key: {}, error: {}", key, error.getMessage()));
    }

    public Mono<Boolean> exists(String key) {
        log.debug("[CACHE_EXISTS] Checking if key exists: {}", key);
        return redisTemplate.hasKey(key)
                .doOnNext(result -> log.debug("[CACHE_EXISTS_RESULT] Key: {}, exists: {}", key, result));
    }
}