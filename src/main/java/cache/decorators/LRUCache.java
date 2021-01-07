package cache.decorators;

import cache.Cache;
import cache.EvictionCache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午3:16
 */
public class LRUCache implements Cache, EvictionCache {

    private Cache decoratedCache;
    private Map<Object, Object> cacheMap;
    private Object removeObject;

    public LRUCache(Cache decoratedCache, Integer size) {
        this.decoratedCache = decoratedCache;
        this.removeObject = null;
        cacheMap = new LinkedHashMap<>(size, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                boolean tooBig =  size() > size;
                if (tooBig) removeObject = eldest.getKey();

                return tooBig;
            }
        };
    }

    @Override
    public String getId() {
        return decoratedCache.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        cacheMap.put(key, value);
        decoratedCache.putObject(key, value);
        if (removeObject != null) {
            cacheMap.remove(removeObject);
        }
    }

    @Override
    public Object getObject(Object key) {
        cacheMap.get(key);
        return decoratedCache.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        cacheMap.remove(key);
        return decoratedCache.removeObject(key);
    }

    @Override
    public void clear() {
        cacheMap.clear();
        decoratedCache.clear();
    }

    @Override
    public int getSize() {
        return decoratedCache.getSize();
    }
}
