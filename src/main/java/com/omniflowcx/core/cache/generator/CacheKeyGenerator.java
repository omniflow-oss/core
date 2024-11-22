package com.omniflowcx.core.cache.generator;

/**
 * Interface for generating cache keys dynamically.
 */
public interface CacheKeyGenerator {
    String generateKey(Object[] parameters, String cacheName);
}
