package transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午5:36
 */
public interface TransactionFactory {

    /**
     * 设置事务工厂自定义属性
     *
     * @param properties 待设置的属性
     */
    default void setProperties(Properties properties) {}

    /**
     * 从现有连接中创建一个 {@link Transaction}
     *
     * @param conn 现有数据库连接
     * @return Transaction
     */
    Transaction newTransaction(Connection conn);

    /**
     * 从数据源创建一个 {@link Transaction}
     *
     * @param dataSource 从中获取连接的数据源
     * @param autoCommit 是否自动提交
     * @return Transaction
     */
    Transaction newTransaction(DataSource dataSource, boolean autoCommit);

}
