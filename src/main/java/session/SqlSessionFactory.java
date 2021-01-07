package session;

import java.sql.Connection;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:51
 */
public interface SqlSessionFactory {

    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

    SqlSession openSession(Connection connection);

    Configuration getConfiguration();

}
