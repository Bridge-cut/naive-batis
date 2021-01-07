package transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午5:37
 */
public interface Transaction {

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     * @throws SQLException the SQL exception
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交数据库连接
     *
     * @throws SQLException the SQL exception
     */
    void commit() throws SQLException;

    /**
     * 回滚数据库连接
     *
     * @throws SQLException the SQL exception
     */
    void rollback() throws SQLException;

    /**
     * 关闭内部数据库连接
     *
     * @throws SQLException the SQL exception
     */
    void close() throws SQLException;

    /**
     * 获取事务超时（如果已设置）
     *
     * @return 超时时间
     * @throws SQLException the SQL exception
     */
    Integer getTimeout() throws SQLException;

}
