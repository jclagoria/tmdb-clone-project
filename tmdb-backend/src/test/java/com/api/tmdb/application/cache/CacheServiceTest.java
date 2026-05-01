package com.api.tmdb.application.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations;

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService(redisTemplate);
    }

    @Test
    void get_shouldReturnCachedValue_whenKeyExists() {
        String cacheKey = "trending:day:en-US";
        String cachedValue = "{\"page\":1,\"results\":[]}";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(Mono.just(cachedValue));

        StepVerifier.create(cacheService.get(cacheKey, String.class))
                .expectNext(cachedValue)
                .verifyComplete();
    }

    @Test
    void get_shouldReturnEmpty_whenKeyNotExists() {
        String cacheKey = "trending:week:en-US";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(Mono.empty());

        StepVerifier.create(cacheService.get(cacheKey, String.class))
                .verifyComplete();
    }

    @Test
    void set_shouldStoreValueWithTtl() {
        String cacheKey = "trending:day:en-US";
        String value = "{\"page\":1}";
        Duration ttl = Duration.ofMinutes(30);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(eq(cacheKey), eq(value), eq(ttl))).thenReturn(Mono.just(true));

        StepVerifier.create(cacheService.set(cacheKey, value, ttl))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void set_shouldReturnFalse_whenSetFails() {
        String cacheKey = "trending:day:en-US";
        String value = "{\"page\":1}";
        Duration ttl = Duration.ofMinutes(30);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(any(), any(), any())).thenReturn(Mono.error(new RuntimeException("Redis error")));

        StepVerifier.create(cacheService.set(cacheKey, value, ttl))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void delete_shouldRemoveKey() {
        String cacheKey = "trending:day:en-US";

        when(redisTemplate.delete(cacheKey)).thenReturn(Mono.just(1L));

        StepVerifier.create(cacheService.delete(cacheKey))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void exists_shouldReturnTrue_whenKeyExists() {
        String cacheKey = "trending:day:en-US";

        when(redisTemplate.hasKey(cacheKey)).thenReturn(Mono.just(true));

        StepVerifier.create(cacheService.exists(cacheKey))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void exists_shouldReturnFalse_whenKeyNotExists() {
        String cacheKey = "trending:day:en-US";

        when(redisTemplate.hasKey(cacheKey)).thenReturn(Mono.just(false));

        StepVerifier.create(cacheService.exists(cacheKey))
                .expectNext(false)
                .verifyComplete();
    }
}