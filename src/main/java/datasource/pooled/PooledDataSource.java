package datasource.pooled;

import datasource.BaseDataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:42
 */
public class PooledDataSource extends BaseDataSource {

    private final PoolState state = new PoolState(this);

    private int poolMaximumActiveConnections = 10;
    private int poolMaximumIdleConnections = 5;
    private int poolMaximumCheckoutTime = 20000;
    private int poolTimeToWait = 20000;

    public PooledDataSource() {}

    public PoolState getState() {
        return state;
    }

    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
    }

    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
    }

    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(getUsername(), getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    private PooledConnection popConnection(String username, String password) throws SQLException {
        PooledConnection connection = null;

        while (connection == null) {
            synchronized (state) {
                if (!state.getIdleConnections().isEmpty()) {
                    connection = state.getIdleConnections().remove(0);
                } else {
                    if (state.getActiveConnections().size() < poolMaximumActiveConnections) {
                        connection = new PooledConnection(getRealConnection(), this);
                    } else {
                        PooledConnection oldestActiveConnection = state.getActiveConnections().get(0);
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                        // 年龄最大的连接已经达到最大可回收时间
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
                            state.getActiveConnections().remove(oldestActiveConnection);
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                                oldestActiveConnection.getRealConnection().rollback();
                            }

                            connection = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                            connection.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
                            connection.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
                            oldestActiveConnection.invalidate();
                        } else {
                            try {
                                state.wait(poolTimeToWait);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (connection != null) {
                    if (connection.isValid()) {
                        connection.setCheckoutTimestamp(System.currentTimeMillis());
                        connection.setLastUsedTimestamp(System.currentTimeMillis());
                        state.getActiveConnections().add(connection);
                    }
                }
            }
        }

        return connection;
    }

    protected void pushConnection(PooledConnection connection) {

    }
}
