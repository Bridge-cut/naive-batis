package executor.statement;

import executor.Executor;
import executor.resultset.ResultSetHandler;
import mapping.BoundSql;
import mapping.MappedStatement;
import session.Configuration;
import session.ResultHandler;
import session.RowBounds;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午5:19
 */
public class DefaultStatementHandler implements StatementHandler {

    private final Configuration configuration;

    private final Executor executor;
    private final MappedStatement mappedStatement;
    private final RowBounds rowBounds;
    private final ResultSetHandler resultSetHandler;

    private BoundSql boundSql;

    public DefaultStatementHandler(Executor executor, MappedStatement mappedStatement,
                                   Object parameterObject, RowBounds rowBounds,
                                   ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.rowBounds = rowBounds;

        if (boundSql == null) {
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }

        this.boundSql = boundSql;

        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement,
                rowBounds, resultHandler, boundSql);
    }

    @Override
    public PreparedStatement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        String sql = boundSql.getSql();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            setStatementTimeout(statement, transactionTimeout);
            setFetchSize(statement);

            return statement;
        } catch (SQLException sqlException) {
            closeStatement(statement);
            throw sqlException;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parameterize(Statement statement) throws SQLException {
        List<String> namedParameterIndex = boundSql.getNamedParameterIndex();
        Map<String, Object> parameterObject = (Map<String, Object>) boundSql.getParameterObject();

        int length = namedParameterIndex.size();
        for (int i = 0; i < length; i++) {
            parameterize((PreparedStatement) statement, i + 1, parameterObject.get(namedParameterIndex.get(i)));
        }
    }

    private void parameterize(PreparedStatement statement, Integer index, Object value) throws SQLException {
        if (value == null) statement.setNull(index, Types.VARCHAR);
        else {
            Class<?> clazz = value.getClass();
            if (Float.class.equals(clazz)) statement.setFloat(index, (Float) value);
            if (Double.class.equals(clazz)) statement.setDouble(index, (Double) value);
            if (Long.class.equals(clazz)) statement.setLong(index, (Long) value);
            if (Integer.class.equals(clazz)) statement.setInt(index, (Integer) value);
            if (Boolean.class.equals(clazz)) statement.setBoolean(index, (Boolean) value);
            if (Short.class.equals(clazz)) statement.setShort(index, (Short) value);
            if (String.class.equals(clazz)) statement.setString(index, (String) value);
        }
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) statement;
        preparedStatement.execute();
        return preparedStatement.getUpdateCount();
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) statement;
        preparedStatement.execute();
        return resultSetHandler.handleResultSets(preparedStatement);
    }

    @Override
    public BoundSql getBoundSql() {
        return boundSql;
    }

    private void setStatementTimeout(Statement statement, Integer transactionTimeout) throws SQLException {
        Integer queryTimeout = null;
        if (mappedStatement.getTimeout() != null) queryTimeout = mappedStatement.getTimeout();
        else if (configuration.getDefaultStatementTimeout() != null) {
            queryTimeout = configuration.getDefaultStatementTimeout();
        }

        if (transactionTimeout != null) {
            if (queryTimeout == null || transactionTimeout < queryTimeout) {
                queryTimeout = transactionTimeout;
            }
        }

        if (queryTimeout != null) {
            statement.setQueryTimeout(queryTimeout);
        }
    }

    private void setFetchSize(Statement statement) throws SQLException {
        Integer fetchSize = mappedStatement.getFetchSize() == null ?
                configuration.getDefaultFetchSize() : mappedStatement.getFetchSize();
        if (fetchSize != null) {
            statement.setFetchSize(fetchSize);
        }
    }

    private void closeStatement(Statement statement) {
        try {
            if (statement != null) statement.close();
        } catch (SQLException ignored) {}
    }
}
