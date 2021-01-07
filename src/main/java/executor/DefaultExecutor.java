package executor;

import cache.CacheKey;
import cache.impl.PerpetualCache;
import executor.statement.StatementHandler;
import mapping.BoundSql;
import mapping.MappedStatement;
import session.Configuration;
import session.ResultHandler;
import session.RowBounds;
import transaction.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static executor.ExecutionPlaceholder.EXECUTION_PLACEHOLDER;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/11 下午4:27
 */
public class DefaultExecutor implements Executor {

    private final Configuration configuration;
    private Transaction transaction;
    private PerpetualCache localCache;

    public DefaultExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.localCache = new PerpetualCache("LocalCache");
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public PerpetualCache getLocalCache() {
        return localCache;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setLocalCache(PerpetualCache localCache) {
        this.localCache = localCache;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        clearLocalCache();
        return doUpdate(ms, parameter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds,
                             ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        List<E> result = (List<E>) localCache.getObject(cacheKey);
        if (result == null) {
            result = queryFromDatabase(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }

        return result;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds,
                             ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
        return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
    }

    private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds,
                                          ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        List<E> list;
        localCache.putObject(key, EXECUTION_PLACEHOLDER);
        try {
            list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
        } finally {
            localCache.removeObject(key);
        }
        localCache.putObject(key, list);

        return list;
    }

    private <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds,
                                ResultHandler resultHandler, BoundSql boundSql) {
        PreparedStatement statement = null;
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this,
                    ms, parameter, rowBounds, resultHandler, boundSql);
            statement = prepareStatement(handler);
            return handler.query(statement, resultHandler);
        } catch (SQLException sqlException) {
            throw new RuntimeException("执行数据查询操作失败，失败原因是: " + sqlException);
        } finally {
            closeStatement(statement);
        }
    }

    public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Statement statement = null;
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this,
                    ms, parameter, RowBounds.DEFAULT, null, null);
            statement = prepareStatement(handler);
            return handler.update(statement);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public void commit(boolean required) throws SQLException {

    }

    @Override
    public void rollback(boolean required) throws SQLException {

    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject,
                                   RowBounds rowBounds, BoundSql boundSql) {
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(ms.getNamespace() + "." + ms.getId());
        cacheKey.update(rowBounds.getOffset());
        cacheKey.update(rowBounds.getLimit());
        cacheKey.update(boundSql.getSql());
        cacheKey.update(boundSql.getParameterObject());

        return cacheKey;
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return false;
    }

    @Override
    public void clearLocalCache() {

    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public void close(boolean forceRollback) {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void setExecutorWrapper(Executor executor) {

    }

    private PreparedStatement prepareStatement(StatementHandler handler) throws SQLException {
        PreparedStatement statement;
        Connection connection = transaction.getConnection();
        statement = handler.prepare(connection, transaction.getTimeout());
        handler.parameterize(statement);

        return statement;
    }

    private void closeStatement(Statement statement) {
        try {
            if (statement != null) statement.close();
        } catch (SQLException ignored) {}
    }
}
