package com.omniflowcx.core.cache.interceptor;

import com.omniflowcx.core.cache.annotations.Cacheable;
import com.omniflowcx.core.cache.generator.CacheKeyGenerator;
import com.omniflowcx.core.cache.generator.CacheKeyGeneratorRegistry;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import io.quarkus.cache.runtime.noop.NoOpCache;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Cacheable(cacheName = "")
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class CacheInterceptor {

    @Inject CacheManager cacheManager;
    @Inject CacheKeyGeneratorRegistry keyGeneratorRegistry;

    @AroundInvoke
    public Object manageCache(InvocationContext context) throws Exception {
        Cacheable annotation = context.getMethod().getAnnotation(Cacheable.class);
        if (annotation == null) {
            return context.proceed();
        }

        String cacheName = annotation.cacheName();
        CacheKeyGenerator keyGenerator = keyGeneratorRegistry.getGenerator(cacheName);

        // Generate cache key
        String cacheKey = keyGenerator.generateKey(context.getParameters(), cacheName);

        // Access cache
        Cache cache = cacheManager.getCache(cacheName).orElseGet(() -> new NoOpCache(cacheName));
        Object cachedValue = cache.get(cacheKey, key -> {
            try {
                return context.proceed();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return cachedValue;
    }
}
