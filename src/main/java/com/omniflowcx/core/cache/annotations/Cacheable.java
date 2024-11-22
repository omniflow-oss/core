package com.omniflowcx.core.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as cacheable with a specific cache name and TTL.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    String cacheName(); // Name of the cache
    long ttl() default 600; // Time-to-live in seconds
}
