package executor;

import cache.CacheKey;
import mapping.BoundSql;
import mapping.MappedStatement;
import session.ResultHandler;
import session.RowBounds;
import transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 上午10:27
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    int update(MappedStatement ms, Object parameter) throws SQLException;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds,
                      ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds,
                      ResultHandler resultHandler) throws SQLException;

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

    boolean isCached(MappedStatement ms, CacheKey key);

    void clearLocalCache();

    Transaction getTransaction();

    void close(boolean forceRollback);

    boolean isClosed();

    void setExecutorWrapper(Executor executor);

}
