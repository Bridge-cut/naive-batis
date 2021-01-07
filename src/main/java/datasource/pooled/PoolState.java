package datasource.pooled;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午9:50
 */
public class PoolState {

    private PooledDataSource dataSource;

    private final List<PooledConnection> idleConnections = new ArrayList<>();
    private final List<PooledConnection> activeConnections = new ArrayList<>();

    public PoolState(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PooledDataSource getDataSource() {
        return dataSource;
    }

    public List<PooledConnection> getIdleConnections() {
        return idleConnections;
    }

    public List<PooledConnection> getActiveConnections() {
        return activeConnections;
    }
}
