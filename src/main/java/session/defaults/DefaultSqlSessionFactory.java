package session.defaults;

import executor.Executor;
import mapping.Environment;
import session.Configuration;
import session.SqlSession;
import session.SqlSessionFactory;
import transaction.Transaction;
import transaction.TransactionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:51
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSessionFromDataSource(false);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return openSessionFromDataSource(autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    private SqlSession openSessionFromDataSource(boolean autoCommit) {
        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            tx = transactionFactory.newTransaction(environment.getDataSource(), autoCommit);
            final Executor executor = configuration.newExecutor(tx);
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } catch (Exception e) {
            closeTransaction(tx);
            throw new RuntimeException("打开会话时出现错误，原因是: " + e, e);
        }
    }

    private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
        return environment.getTransactionFactory();
    }

    private void closeTransaction(Transaction transaction) {
        if (transaction != null) {
            try {
                transaction.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }
}
