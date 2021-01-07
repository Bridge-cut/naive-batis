package scripting;

import builder.BaseBuilder;
import mapping.SqlSource;
import org.dom4j.Element;
import parsing.GenericTokenParser;
import parsing.PropertyTokenHandler;
import scripting.defaults.DefaultSqlSource;
import session.Configuration;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午8:13
 */
public class XMLScriptBuilder extends BaseBuilder {

    private final Element statElement;
    private final Class<?> parameterType;
    private final GenericTokenParser propertyTokenParser;

    public XMLScriptBuilder(Configuration configuration, Element statElement, Class<?> parameterType) {
        super(configuration);
        this.statElement = statElement;
        this.parameterType = parameterType;

        this.propertyTokenParser = new GenericTokenParser("${", "}",
                new PropertyTokenHandler(configuration.getVariables()));
    }

    public SqlSource parseScriptNode() {
        String rawSql = statElement.getTextTrim();
        return new DefaultSqlSource(configuration, propertyTokenParser.parse(rawSql), parameterType);
    }
}
