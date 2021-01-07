package executor.statement;

import mapping.BoundSql;
import session.ResultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午5:11
 */
public interface StatementHandler {

    PreparedStatement prepare(Connection connection, Integer transactionTimeout)
            throws SQLException;

    void parameterize(Statement statement)
            throws SQLException;

    int update(Statement statement)
            throws SQLException;

    <E> List<E> query(Statement statement, ResultHandler resultHandler)
            throws SQLException;

    BoundSql getBoundSql();

}
