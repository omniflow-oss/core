package com.omniflowcx.core.cache.generator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for mapping cache names to specific key generators.
 */
@ApplicationScoped
public class CacheKeyGeneratorRegistry {

    @Inject DefaultCacheKeyGenerator defaultKeyGenerator;
    @Inject CustomCacheKeyGenerator customKeyGenerator;

    private final Map<String, CacheKeyGenerator> registry = new HashMap<>();

    public CacheKeyGeneratorRegistry() {
        registry.put("default", defaultKeyGenerator);
        registry.put("my-custom-cache", customKeyGenerator);
    }

    public CacheKeyGenerator getGenerator(String cacheName) {
        return registry.getOrDefault(cacheName, defaultKeyGenerator);
    }
}