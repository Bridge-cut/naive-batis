package session;

import session.defaults.DefaultSqlSessionFactory;
import builder.xml.XMLConfigBuilder;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:54
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream) {
        return build(inputStream, null, null);
    }

    private SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try (inputStream) {
            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
