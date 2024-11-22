package com.omniflowcx.core.cache.config;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterForReflection
public class CacheConfig {
    public boolean isCacheEnabled() {
        return Boolean.parseBoolean(System.getProperty("cache.enabled", "true"));
    }
}