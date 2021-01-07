package cache.decorators;

import cache.Cache;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午4:43
 */
public class ScheduledCache implements Cache {

    private final Cache decoratedCache;
    protected long clearInterval;
    protected long lastClear;

    public ScheduledCache(Cache decoratedCache, long clearInterval) {
        this.decoratedCache = decoratedCache;
        this.clearInterval = clearInterval;
        this.lastClear = System.currentTimeMillis();
    }

    public void setClearInterval(long clearInterval) {
        this.clearInterval = clearInterval;
    }

    @Override
    public String getId() {
        return decoratedCache.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        scheduledClear();
        decoratedCache.putObject(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return scheduledClear() ? null : decoratedCache.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        scheduledClear();
        return decoratedCache.removeObject(key);
    }

    @Override
    public void clear() {
        lastClear = System.currentTimeMillis();
        decoratedCache.clear();
    }

    @Override
    public int getSize() {
        scheduledClear();
        return decoratedCache.getSize();
    }

    private boolean scheduledClear() {
        if (System.currentTimeMillis() - lastClear > clearInterval) {
            clear();
            return true;
        }

        return false;
    }
}
