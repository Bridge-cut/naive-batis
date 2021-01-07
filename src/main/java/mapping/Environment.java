package mapping;

import transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:44
 */
public class Environment {

    private final String id;
    private final TransactionFactory transactionFactory;
    private DataSource dataSource;

    public Environment(String id, TransactionFactory txFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = txFactory;
        this.dataSource = dataSource;
    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
