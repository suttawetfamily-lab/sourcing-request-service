package com.pantavanij.sourcingreq.services.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("userTimeZone"),
                new ConcurrentMapCache("requestGridFieldDtoList")
                ));
        return cacheManager;
    }

    @CacheEvict(value = "userTimeZone", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.userTimeZoneTTL}")
    public void emptyUserTimeZoneCache() {
    }


}
