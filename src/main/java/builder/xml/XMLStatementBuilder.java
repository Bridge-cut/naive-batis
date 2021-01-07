package builder.xml;

import builder.BaseBuilder;
import builder.MapperBuilderAssistant;
import mapping.SqlCommandType;
import mapping.SqlSource;
import org.dom4j.Element;
import scripting.XMLScriptBuilder;
import session.Configuration;

import java.util.Locale;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/11 下午5:33
 */
public class XMLStatementBuilder extends BaseBuilder {

    private final MapperBuilderAssistant builderAssistant;
    private final Element statElement;

    public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant,
                               Element statElement) {
        super(configuration);

        this.builderAssistant = builderAssistant;
        this.statElement = statElement;
    }

    public MapperBuilderAssistant getBuilderAssistant() {
        return builderAssistant;
    }

    public Element getStatElement() {
        return statElement;
    }

    public void parseStatementNode() {
        String namespace = statElement.getParent().attributeValue("namespace");
        String id = statElement.attributeValue("id");

        String nodeName = statElement.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        boolean useCache = Boolean.parseBoolean(statElement.attributeValue("useCache", String.valueOf(isSelect)));
        boolean flushCache = Boolean.parseBoolean(statElement.attributeValue("flushCache", "true"));
        flushCache = isSelect && flushCache;

        String parameterType = statElement.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveClass(parameterType);

        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, statElement, parameterTypeClass);
        SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
        String fetchSizeValue = statElement.attributeValue("fetchSize");
        Integer fetchSize = fetchSizeValue != null ? Integer.valueOf(fetchSizeValue) : null;
        String timeoutValue = statElement.attributeValue("timeout");
        Integer timeout = timeoutValue != null ? Integer.valueOf(timeoutValue) : null;
        String resultType = statElement.attributeValue("resultType");
        Class<?> resultTypeClass = resolveClass(resultType);


        builderAssistant.addMappedStatement(id, resultTypeClass,
                sqlSource, sqlCommandType, fetchSize, timeout, flushCache, useCache);
    }
}
