package com.saas.saas.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Spring Cache abstraction configuration class.
 * Annotating with @EnableCaching bootstraps the Spring Cache infrastructure,
 * enabling processing of annotations like @Cacheable, @CacheEvict, and @CachePut.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Exposes a CacheManager utilizing in-memory ConcurrentHashMap caches.
     * Easily extendable to Redis in clustered environments.
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(List.of("tenants"));
        return cacheManager;
    }
}
