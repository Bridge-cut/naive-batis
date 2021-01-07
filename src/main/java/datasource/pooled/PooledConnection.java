package datasource.pooled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午9:50
 */
public class PooledConnection implements InvocationHandler {

    private static final String CLOSE = "close";
    private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };

    private final int hashCode;
    private final PooledDataSource dataSource;
    private final Connection realConnection;
    private final Connection proxyConnection;
    private long checkoutTimestamp;
    private long createdTimestamp;
    private long lastUsedTimestamp;
    private boolean valid;

    public PooledConnection(Connection connection, PooledDataSource dataSource) {
        this.hashCode = connection.hashCode();
        this.realConnection = connection;
        this.dataSource = dataSource;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
    }

    public void invalidate() {
        valid = false;
    }

    public int getHashCode() {
        return hashCode;
    }

    public PooledDataSource getDataSource() {
        return dataSource;
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public void setCheckoutTimestamp(long checkoutTimestamp) {
        this.checkoutTimestamp = checkoutTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public boolean isValid() {
        return valid && realConnection != null;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (CLOSE.equals(methodName)) {
            dataSource.pushConnection(this);
            return null;
        }

        return method.invoke(realConnection, args);
    }
}
