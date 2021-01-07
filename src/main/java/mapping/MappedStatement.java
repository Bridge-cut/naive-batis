package mapping;

import cache.Cache;
import session.Configuration;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:45
 */
public class MappedStatement {

    private String id;
    private String namespace;
    private Configuration configuration;
    private Class<?> resultTypeClass;
    private SqlSource sqlSource;
    private Cache cache;
    private SqlCommandType sqlCommandType;
    private Integer fetchSize;
    private Integer timeout;
    private boolean useCache;
    private boolean flushCacheRequired;

    public MappedStatement() {}

    public MappedStatement(String id, String namespace, Configuration configuration,
                           Class<?> resultTypeClass, SqlSource sqlSource, SqlCommandType sqlCommandType,
                           Integer fetchSize, Integer timeout, boolean flushCacheRequired) {
        this.id = id;
        this.namespace = namespace;
        this.configuration = configuration;
        this.resultTypeClass = resultTypeClass;
        this.sqlSource = sqlSource;
        this.sqlCommandType = sqlCommandType;
        this.fetchSize = fetchSize;
        this.timeout = timeout;
        this.flushCacheRequired = flushCacheRequired;
    }

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, String namespace,
                       SqlSource sqlSource, SqlCommandType sqlCommandType) {
            mappedStatement.setId(id);
            mappedStatement.setNamespace(namespace);
            mappedStatement.setConfiguration(configuration);
            mappedStatement.setSqlSource(sqlSource);
            mappedStatement.setSqlCommandType(sqlCommandType);
        }

        public Builder fetchSize(Integer fetchSize) {
            mappedStatement.setFetchSize(fetchSize);
            return this;
        }

        public Builder timeout(Integer timeout) {
            mappedStatement.setTimeout(timeout);
            return this;
        }

        public Builder cache(Cache cache) {
            mappedStatement.setCache(cache);
            return this;
        }

        public Builder flushCacheRequired(boolean flushCacheRequired) {
            mappedStatement.setFlushCacheRequired(flushCacheRequired);
            return this;
        }

        public Builder useCache(boolean useCache) {
            mappedStatement.setUseCache(useCache);
            return this;
        }

        public Builder resultTypeClass(Class<?> resultTypeClass) {
            mappedStatement.setResultTypeClass(resultTypeClass);
            return this;
        }

        public MappedStatement build() {
            return mappedStatement;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Class<?> getResultTypeClass() {
        return resultTypeClass;
    }

    public void setResultTypeClass(Class<?> resultTypeClass) {
        this.resultTypeClass = resultTypeClass;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isFlushCacheRequired() {
        return flushCacheRequired;
    }

    public void setFlushCacheRequired(boolean flushCacheRequired) {
        this.flushCacheRequired = flushCacheRequired;
    }

    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }
}
