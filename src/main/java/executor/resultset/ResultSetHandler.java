package executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午8:10
 */
public interface ResultSetHandler {

    <E> List<E> handleResultSets(Statement stmt) throws SQLException;

}
