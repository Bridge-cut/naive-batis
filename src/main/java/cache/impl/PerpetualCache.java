package cache.impl;

import cache.Cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午3:10
 */
public class PerpetualCache implements Cache {

    // namespace
    private String id;
    private Map<Object, Object> cache = new HashMap<>();

    public PerpetualCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return cache.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            throw new RuntimeException("缓存组件必须指定 id");
        }

        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (getId() == null) {
            throw new RuntimeException("缓存组件必须指定 id");
        }

        if (this == obj) return true;
        if (!(obj instanceof Cache)) return false;

        Cache otherCache = (Cache) obj;
        return getId().equals(otherCache.getId());
    }
}
