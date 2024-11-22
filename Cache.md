# Omniflow Cache Library

## Overview

The **Omniflow Cache Library** simplifies caching for Quarkus microservices by providing:
- **Annotations** to mark methods as cacheable.
- **Dynamic cache key generation** with custom strategies.
- **Flexible configuration** for enabling/disabling cache and controlling behavior.

This library extends the default Quarkus Cache API to support advanced use cases like:
- Generating cache keys dynamically based on method parameters.
- Using custom cache key generators.
- Supporting TTL (Time-to-Live) for cached entries.

---

## Features

- **`@Cacheable`**: Marks methods as cacheable with a specified cache name and TTL.
- **`@CacheKey`**: Marks method parameters to be included in the cache key.
- **Custom Cache Key Generators**: Define custom strategies for generating cache keys.
- **Dynamic Registry**: Maps cache names to specific key generators.
- **Configuration**: Enable or disable caching globally or per service via `application.properties`.

---

## Installation

Add the library dependency to your Quarkus project:

```xml
<dependency>
    <groupId>com.omniflowcx.core</groupId>
    <artifactId>cache</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Annotations

### `@Cacheable`

Marks a method as cacheable and specifies the cache name and optional TTL (default: 600 seconds).

#### Parameters:
- **`cacheName`**: (Required) Name of the cache.
- **`ttl`**: (Optional) Time-to-live in seconds.

#### Example:
```java
@Cacheable(cacheName = "my-cache", ttl = 300)
public String getData(String param) {
    return "Data for " + param;
}
```

---

### `@CacheKey`

Marks method parameters to be included in the cache key. Use this to construct more specific cache keys.

#### Example:
```java
@Cacheable(cacheName = "user-data-cache", ttl = 600)
public String getUserData(@CacheKey String userId, int age) {
    return "User data for " + userId + " aged " + age;
}
```
In this example, `userId` is part of the cache key.

---

## Cache Key Generators

### **DefaultCacheKeyGenerator**

Automatically generates keys based on all method parameters.

#### Key Format:
`<cacheName>::<param1>::<param2>::...`

#### Example:
For a method `getData("value1", 42)`:
- Cache key: `my-cache::value1::42`

---

### **CustomCacheKeyGenerator**

Implements a custom strategy for generating cache keys. For example, prefixing cache keys with a custom value.

#### Example:
```java
@ApplicationScoped
public class CustomCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public String generateKey(Object[] parameters, String cacheName) {
        if (parameters.length > 0 && parameters[0] instanceof String) {
            return cacheName + "::customPrefix::" + parameters[0];
        }
        return cacheName + "::defaultKey";
    }
}
```

---

## Configuration

Use the following configurations in `application.properties`:

- **`cache.enabled`**: Enable or disable caching globally. Default: `true`.
- **`cache.ttl.default`**: Default TTL for cache entries (in seconds). Default: `600`.

#### Example:
```properties
cache.enabled=true
cache.ttl.default=600
```

---

## Example Usage

### **1. Basic Caching**

#### Code Example:
```java
import com.omniflowcx.core.cache.annotations.Cacheable;

public class MyService {

    @Cacheable(cacheName = "basic-cache", ttl = 300)
    public String getData(String param) {
        return "Data for " + param;
    }
}
```

#### Workflow:
1. Call `getData("value1")`.
2. The result is cached under the key: `basic-cache::value1`.
3. Subsequent calls return the cached value for 300 seconds.

---

### **2. Cache Key Customization**

#### Code Example:
```java
import com.omniflowcx.core.cache.annotations.Cacheable;
import com.omniflowcx.core.cache.annotations.CacheKey;

public class MyService {

    @Cacheable(cacheName = "custom-cache", ttl = 300)
    public String getUserData(@CacheKey String userId, int age) {
        return "User data for " + userId + " aged " + age;
    }
}
```

#### Workflow:
1. Call `getUserData("user123", 25)`.
2. The result is cached under the key: `custom-cache::user123`.
3. Subsequent calls with the same `userId` return the cached value.

---

### **3. Using Custom Cache Key Generator**

#### Custom Generator:
```java
@ApplicationScoped
public class UserCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public String generateKey(Object[] parameters, String cacheName) {
        if (parameters.length > 0) {
            return cacheName + "::user::" + parameters[0];
        }
        return cacheName + "::default";
    }
}
```

#### Registry Configuration:
```java
@ApplicationScoped
public class CacheKeyGeneratorRegistry {

    @Inject DefaultCacheKeyGenerator defaultKeyGenerator;
    @Inject UserCacheKeyGenerator userKeyGenerator;

    private final Map<String, CacheKeyGenerator> registry = new HashMap<>();

    public CacheKeyGeneratorRegistry() {
        registry.put("user-cache", userKeyGenerator);
        registry.put("default", defaultKeyGenerator);
    }

    public CacheKeyGenerator getGenerator(String cacheName) {
        return registry.getOrDefault(cacheName, defaultKeyGenerator);
    }
}
```

#### Method Usage:
```java
@Cacheable(cacheName = "user-cache", ttl = 600)
public String getUserProfile(String userId) {
    return "Profile data for " + userId;
}
```

#### Workflow:
1. Call `getUserProfile("user123")`.
2. Custom key generator produces the key: `user-cache::user::user123`.
3. The result is cached and reused for 600 seconds.

---

### **4. Dynamic Caching with Headers**

You can extend the library to handle headers dynamically (e.g., `X-Cache-Control`) to bypass or refresh the cache.

#### Example Interceptor:
```java
@Provider
public class CacheControlInterceptor implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) {
        if ("bypass".equals(requestContext.getHeaderString("X-Cache-Control"))) {
            // Invalidate cache logic
        }
    }
}
```

---

## Advanced Topics

### Monitoring Cache Usage

Integrate **Quarkus Metrics** to monitor cache hits, misses, and evictions:
- Add the `quarkus-smallrye-metrics` dependency.
- Enable metrics in `application.properties`:
  ```properties
  quarkus.metrics.enabled=true
  ```

### Extending TTL

You can extend TTL dynamically by updating the cache entry programmatically:
```java
cacheManager.getCache("my-cache").ifPresent(cache -> {
        cache.put("key", "value", ttlInSeconds);
});
```

---

## Summary

This library simplifies caching in Quarkus microservices with:
- Annotation-based configuration (`@Cacheable`, `@CacheKey`).
- Dynamic cache key generation.
- Customizable key generators for domain-specific logic.
- Flexible configurations and monitoring capabilities.

Integrate it into your microservices to optimize performance and scalability!