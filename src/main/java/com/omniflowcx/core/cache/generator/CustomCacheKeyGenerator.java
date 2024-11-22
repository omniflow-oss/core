package com.omniflowcx.core.cache.generator;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Example custom implementation for generating cache keys.
 */
@ApplicationScoped
public class CustomCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public String generateKey(Object[] parameters, String cacheName) {
        if (parameters.length > 0 && parameters[0] instanceof String) {
            return cacheName + "::customKey::" + parameters[0];
        }
        return cacheName + "::defaultKey";
    }
}