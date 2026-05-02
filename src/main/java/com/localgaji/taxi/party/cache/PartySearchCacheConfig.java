package com.localgaji.taxi.party.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.localgaji.taxi.party.dto.ResponseParty.*;

@EnableCaching
@Configuration
public class PartySearchCacheConfig {

    public static final String PARTY_SEARCH_CACHE = "partySearch";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(PARTY_SEARCH_CACHE);
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(5_000)
                        .expireAfterWrite(Duration.ofMinutes(5))
        );
        return cacheManager;
    }

    @Bean
    public Cache<PartySearchCacheKey, GetPartiesSearchRes> partySearchCache() {
        return Caffeine.newBuilder()
                .maximumSize(5_000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }
}