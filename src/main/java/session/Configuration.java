package session;

import binding.MapperRegistry;
import cache.Cache;
import cache.decorators.FIFOCache;
import cache.decorators.LRUCache;
import cache.impl.PerpetualCache;
import datasource.pooled.PooledDataSourceFactory;
import executor.DefaultExecutor;
import executor.Executor;
import executor.resultset.DefaultResultSetHandler;
import executor.resultset.ResultSetHandler;
import executor.statement.DefaultStatementHandler;
import executor.statement.StatementHandler;
import mapping.BoundSql;
import mapping.Environment;
import mapping.MappedStatement;
import plugin.InterceptorChain;
import reflection.ReflectorFactory;
import reflection.impl.DefaultReflectorFactory;
import transaction.Transaction;
import transaction.jdbc.JdbcTransactionFactory;
import type.TypeAliasRegistry;

import java.util.*;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:39
 */
public class Configuration {

    // <properties>
    private Properties variables = new Properties();

    // <settings>
    private boolean useGeneratedKeys;
    private boolean mapUnderscoreToCamelCase;
    private Integer defaultStatementTimeout;
    private Integer defaultFetchSize;

    // <typeAliases>
    private TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    // <environments>
    private Environment environment;

    // <key=namespace, value=该 namespace 对应的二级缓存>
    private final Map<String, Cache> caches = new HashMap<>();

    private final Set<String> loadedResources = new HashSet<>();
    private MapperRegistry mapperRegistry = new MapperRegistry(this);
    private Map<String, MappedStatement> statementMap = new HashMap<>();
    private final InterceptorChain interceptorChain = new InterceptorChain();
    private ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    /**
     * 通过 namespace + id 获取对应的 MappedStatement
     *
     * @param statement namespace + id
     * @return 对应的 MappedStatement
     */
    public MappedStatement getMappedStatement(String statement) {
        return statementMap.get(statement);
    }

    public void addMappedStatement(String statement, MappedStatement mappedStatement) {
        statementMap.put(statement, mappedStatement);
    }

    public Configuration() {
        // jdbc
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);

        // cache
        typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
        typeAliasRegistry.registerAlias("LRU", LRUCache.class);
        typeAliasRegistry.registerAlias("FIFO", FIFOCache.class);
    }

    public Properties getVariables() {
        return variables;
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
        this.defaultStatementTimeout = defaultStatementTimeout;
    }

    public Integer getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public void setDefaultFetchSize(Integer defaultFetchSize) {
        this.defaultFetchSize = defaultFetchSize;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public void setTypeAliasRegistry(TypeAliasRegistry typeAliasRegistry) {
        this.typeAliasRegistry = typeAliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Map<String, Cache> getCaches() {
        return caches;
    }

    public Set<String> getLoadedResources() {
        return loadedResources;
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public void setMapperRegistry(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    public Map<String, MappedStatement> getStatementMap() {
        return statementMap;
    }

    public void setStatementMap(Map<String, MappedStatement> statementMap) {
        this.statementMap = statementMap;
    }

    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public void setReflectorFactory(ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public void addCache(Cache cache) {
        caches.put(cache.getId(), cache);
    }

    public Executor newExecutor(Transaction transaction) {
        Executor executor = new DefaultExecutor(this, transaction);
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement,
                                                Object parameterObject, RowBounds rowBounds,
                                                ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new DefaultStatementHandler(executor, mappedStatement,
                parameterObject, rowBounds, resultHandler, boundSql);
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }

    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds,
                                                ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement,
                resultHandler, boundSql, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }
}
