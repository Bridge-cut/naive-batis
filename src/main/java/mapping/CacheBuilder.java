package mapping;

import cache.Cache;
import cache.EvictionCache;
import cache.decorators.LRUCache;
import cache.decorators.ScheduledCache;
import cache.impl.PerpetualCache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午4:06
 */
public class CacheBuilder {

    private final String namespace;
    private final List<Class<? extends Cache>> decorators;
    private Class<? extends Cache> implementation;
    private Long flushInterval;
    private Integer size;

    public CacheBuilder(String namespace) {
        this.namespace = namespace;
        this.decorators = new ArrayList<>();
    }


    public CacheBuilder implementation(Class<? extends Cache> implementation) {
        this.implementation = implementation;
        return this;
    }

    public CacheBuilder addDecorator(Class<? extends Cache> decorator) {
        if (decorator != null) {
            decorators.add(decorator);
        }
        return this;
    }

    public CacheBuilder clearInterval(Long flushInterval) {
        this.flushInterval = flushInterval;
        return this;
    }

    public CacheBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    public Cache build() {
        setDefaultImplementations();
        Cache baseCache = newBaseCacheInstance(implementation, namespace);
        for (Class<? extends Cache> decorator : decorators) {
            baseCache = decorateBaseCache(decorator, baseCache);
        }

        return setStandardDecorators(baseCache);
    }

    private void setDefaultImplementations() {
        if (implementation == null) {
            implementation = PerpetualCache.class;
            if (decorators.isEmpty()) {
                decorators.add(LRUCache.class);
            }
        }
    }

    private Cache newBaseCacheInstance(Class<? extends Cache> implementation, String namespace) {
        try {
            Constructor<? extends Cache> baseConstructor = implementation.getDeclaredConstructor(String.class);
            return baseConstructor.newInstance(namespace);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(implementation + " 不存在参数为 String.class 的构造器" + e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("无法调用 " + implementation + " 的构造方法，原因是: " + e, e);
        }
    }

    private Cache decorateBaseCache(Class<? extends Cache> decorator, Cache baseCache) {
        if (EvictionCache.class.isAssignableFrom(decorator)) {
            return decorateEvictionCache(decorator, baseCache);
        } else return decorateOtherCache(decorator, baseCache);
    }

    private Cache decorateEvictionCache(Class<? extends Cache> decorator, Cache baseCache) {
        try {
            Constructor<? extends Cache> evictionConstructor =
                    decorator.getDeclaredConstructor(Cache.class, Integer.class);
            return evictionConstructor.newInstance(baseCache, size);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(decorator + " 不存在参数为 (Cache.class, Integer.class) 的构造器" + e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("无法调用 " + decorator + " 的构造方法，原因是: " + e, e);
        }
    }

    private Cache decorateOtherCache(Class<? extends Cache> decorator, Cache baseCache) {
        try {
            Constructor<? extends Cache> otherConstructor =
                    decorator.getDeclaredConstructor(Cache.class);
            return otherConstructor.newInstance(baseCache);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(decorator + " 不存在参数为 Cache.class 的构造器" + e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("无法调用 " + decorator + " 的构造方法，原因是: " + e, e);
        }
    }

    private Cache setStandardDecorators(Cache baseCache) {
        return new ScheduledCache(baseCache, flushInterval);
    }
}
