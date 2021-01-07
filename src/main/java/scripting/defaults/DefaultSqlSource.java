package scripting.defaults;

import mapping.BoundSql;
import mapping.SqlSource;
import session.Configuration;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/13 下午2:56
 */
public class DefaultSqlSource implements SqlSource {

    private final String namedSql;
    private final Configuration configuration;
    private final Class<?> parameterType;

    public DefaultSqlSource(Configuration configuration, String namedSql, Class<?> parameterType) {
        this.namedSql = namedSql;
        this.configuration = configuration;
        this.parameterType = parameterType;
    }

    public String getNamedSql() {
        return namedSql;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(namedSql, parameterObject);
    }
}
