package cache.decorators;

import cache.Cache;
import cache.EvictionCache;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午3:39
 */
public class FIFOCache implements Cache, EvictionCache {

    private int size;
    private Cache decoratedCache;
    // key 的 Queue
    private Queue<Object> cacheQueue;

    public FIFOCache(Cache decoratedCache, Integer size) {
        this.size = size;
        this.decoratedCache = decoratedCache;
        this.cacheQueue = new LinkedList<>();
    }

    @Override
    public String getId() {
        return decoratedCache.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        decoratedCache.putObject(key, value);
        if (cacheQueue.size() > size) {
            Object removeKey = cacheQueue.poll();
            decoratedCache.removeObject(removeKey);
        }
    }

    @Override
    public Object getObject(Object key) {
        return decoratedCache.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        cacheQueue.remove(key);
        return decoratedCache.removeObject(key);
    }

    @Override
    public void clear() {
        cacheQueue.clear();
        decoratedCache.clear();
    }

    @Override
    public int getSize() {
        return decoratedCache.getSize();
    }
}
