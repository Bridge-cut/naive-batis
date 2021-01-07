package session;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:31
 */
public interface SqlSession extends Closeable {

    /**
     * Retrieve a single row mapped from the statement key.
     * @param <T> the returned object type
     * @param statement
     *          the statement
     * @return Mapped object
     */
    <T> T selectOne(String statement);

    /**
     * Retrieve a single row mapped from the statement key and parameter.
     * @param <T> the returned object type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return Mapped object
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * Retrieve a list of mapped objects from the statement key.
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement);

    /**
     * Retrieve a list of mapped objects from the statement key and parameter.
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement, Object parameter);

    /**
     * Retrieve a list of mapped objects from the statement key and parameter,
     * within the specified row bounds.
     * @param <E> the returned list element type
     * @param statement Unique identifier matching the statement to use.
     * @param parameter A parameter object to pass to the statement.
     * @param rowBounds  Bounds to limit object retrieval
     * @return List of mapped object
     */
    <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

    /**
     * Execute an insert statement.
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the insert.
     */
    int insert(String statement);

    /**
     * Execute an insert statement with the given parameter object. Any generated
     * autoincrement values or selectKey entries will modify the given parameter
     * object properties. Only the number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the insert.
     */
    int insert(String statement, Object parameter);

    /**
     * Execute an update statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the update.
     */
    int update(String statement);

    /**
     * Execute an update statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the update.
     */
    int update(String statement, Object parameter);

    /**
     * Execute a delete statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @return int The number of rows affected by the delete.
     */
    int delete(String statement);

    /**
     * Execute a delete statement. The number of rows affected will be returned.
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the delete.
     */
    int delete(String statement, Object parameter);

    /**
     * Flushes batch statements and commits database connection.
     * Note that database connection will not be committed if no updates/deletes/inserts were called.
     * To force the commit call {@link SqlSession#commit(boolean)}
     */
    void commit();

    /**
     * Flushes batch statements and commits database connection.
     * @param force forces connection commit
     */
    void commit(boolean force);

    /**
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     * To force the rollback call {@link SqlSession#rollback(boolean)}
     */
    void rollback();

    /**
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     * @param force forces connection rollback
     */
    void rollback(boolean force);

    /**
     * Closes the session.
     */
    @Override
    void close();

    /**
     * Clears local session cache.
     */
    void clearCache();

    /**
     * Retrieves current configuration.
     * @return Configuration
     */
    Configuration getConfiguration();

    /**
     * Retrieves a mapper.
     * @param <T> the mapper type
     * @param type Mapper interface class
     * @return a mapper bound to this SqlSession
     */
    <T> T getMapper(Class<T> type);

    /**
     * Retrieves inner database connection.
     * @return Connection
     */
    Connection getConnection();
}
