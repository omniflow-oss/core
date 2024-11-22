package com.omniflowcx.core.cache.generator;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Default implementation for generating cache keys.
 */
@ApplicationScoped
public class DefaultCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public String generateKey(Object[] parameters, String cacheName) {
        if (parameters == null || parameters.length == 0) {
            return cacheName + "::default";
        }
        return cacheName + "::" + Arrays.stream(parameters)
                .map(Object::toString)
                .collect(Collectors.joining("::"));
    }
}