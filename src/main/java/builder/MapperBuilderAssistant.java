package builder;

import cache.Cache;
import cache.decorators.LRUCache;
import cache.impl.PerpetualCache;
import mapping.CacheBuilder;
import mapping.MappedStatement;
import mapping.SqlCommandType;
import mapping.SqlSource;
import session.Configuration;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/11 下午5:12
 */
public class MapperBuilderAssistant extends BaseBuilder {

    private String currentNamespace;
    private final String resource;
    private Cache currentCache;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public String getResource() {
        return resource;
    }

    public Cache getCurrentCache() {
        return currentCache;
    }

    public void setCurrentNamespace(String currentNamespace) {
        if (currentNamespace == null) {
            throw new RuntimeException("mapper 元素需要指定 namespace 属性");
        }

        if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) {
            throw new RuntimeException("namespace 不符合要求，当前正在解析 "
                    + this.currentNamespace + " 但是持有的是 " + currentNamespace);
        }

        this.currentNamespace = currentNamespace;
    }

    public void useNewCache(Class<? extends Cache> typeClass, Class<? extends Cache> evictionClass,
                            Long flushInterval, Integer size) {
        Cache cache = new CacheBuilder(currentNamespace)
                .implementation(valueOrDefault(typeClass, PerpetualCache.class))
                .addDecorator(valueOrDefault(evictionClass, LRUCache.class))
                .clearInterval(flushInterval)
                .size(size)
                .build();
        configuration.addCache(cache);
        currentCache = cache;
    }

    public void addMappedStatement(String id, Class<?> resultTypeClass,
                                   SqlSource sqlSource, SqlCommandType sqlCommandType,
                                   Integer fetchSize, Integer timeout,
                                   boolean flushCache, boolean useCache) {
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

        MappedStatement.Builder statementBuilder =
                new MappedStatement.Builder(configuration, id, currentNamespace, sqlSource, sqlCommandType)
                        .resultTypeClass(resultTypeClass)
                        .fetchSize(fetchSize)
                        .timeout(timeout)
                        .flushCacheRequired(valueOrDefault(flushCache, !isSelect))
                        .useCache(valueOrDefault(useCache, isSelect))
                        .cache(currentCache);

        MappedStatement statement = statementBuilder.build();
        configuration.addMappedStatement(currentNamespace + "." + id, statement);
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
