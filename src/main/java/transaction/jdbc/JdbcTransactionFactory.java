package transaction.jdbc;

import transaction.Transaction;
import transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午5:42
 */
public class JdbcTransactionFactory implements TransactionFactory {

    private boolean autoCommit = true;

    @Override
    public void setProperties(Properties properties) {
        if (properties != null) {
            String autoCommitProperty = properties.getProperty("autoCommit");
            if (autoCommitProperty != null) {
                autoCommit = Boolean.parseBoolean(autoCommitProperty);
            }
        }
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, boolean autoCommit) {
        return new JdbcTransaction(dataSource, autoCommit);
    }
}
