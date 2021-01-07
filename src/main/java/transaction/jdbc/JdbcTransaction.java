package transaction.jdbc;

import transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午5:42
 */
public class JdbcTransaction implements Transaction {

    private Connection connection;
    private DataSource dataSource;
    private boolean autoCommit;

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    public JdbcTransaction(DataSource ds, boolean desiredAutoCommit) {
        this.dataSource = ds;
        this.autoCommit = desiredAutoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }

        return connection;
    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }

    private void openConnection() {
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(autoCommit);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }
}
