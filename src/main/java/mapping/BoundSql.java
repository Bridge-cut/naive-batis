package mapping;

import parsing.GenericTokenParser;
import parsing.NamedTokenHandler;

import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午7:51
 */
public class BoundSql {

    private final String sql;

    private final Object parameterObject;
    private final List<String> namedParameterIndex;
    private final GenericTokenParser genericTokenParser;

    public BoundSql(String namedSql, Object parameterObject) {
        this.parameterObject = parameterObject;

        NamedTokenHandler namedTokenHandler = new NamedTokenHandler();
        this.genericTokenParser = new GenericTokenParser("#{", "}", namedTokenHandler);
        this.sql = genericTokenParser.parse(namedSql);
        this.namedParameterIndex = namedTokenHandler.getNamedParameterIndex();
    }

    public String getSql() {
        return sql;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public List<String> getNamedParameterIndex() {
        return namedParameterIndex;
    }

    public GenericTokenParser getGenericTokenParser() {
        return genericTokenParser;
    }
}
