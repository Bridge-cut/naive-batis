package cache;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午3:06
 */
public interface Cache {

    /**
     * @return 缓存的标识
     */
    String getId();

    /**
     * 缓存一个对象
     *
     * @param key 缓存对象对应的 key
     * @param value 缓存对象
     */
    void putObject(Object key, Object value);

    /**
     * 获取单个缓存对象
     *
     * @param key 待获取的缓存对象的 key
     * @return 获取到的缓存对象
     */
    Object getObject(Object key);

    /**
     * 移除单个缓存对象
     *
     * @param key 待移除的缓存对象的 key
     * @return 被移除的缓存对象
     */
    Object removeObject(Object key);

    /**
     * 清除缓存
     */
    void clear();

    /**
     * 获取缓存中对象数目
     *
     * @return 存储在缓存中的元素数
     */
    int getSize();

}
