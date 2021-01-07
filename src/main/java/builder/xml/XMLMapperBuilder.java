package builder.xml;

import builder.BaseBuilder;
import builder.MapperBuilderAssistant;
import cache.Cache;
import io.Resources;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import session.Configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/11 上午10:38
 */
public class XMLMapperBuilder extends BaseBuilder {

    private final String resource;
    private final InputStream inputStream;
    private final Configuration configuration;
    private final SAXReader saxReader;
    private final MapperBuilderAssistant builderAssistant;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) {
        super(configuration);

        this.inputStream = inputStream;
        this.configuration = configuration;
        this.resource = resource;

        this.saxReader = new SAXReader();
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
    }

    public void parse() {
        if (!configuration.isResourceLoaded(resource)) {
            configuration.addLoadedResource(resource);
            parseMapper();
        }
    }

    private void parseMapper() {
        System.err.println("开始按标签顺序逐个解析 " + resource);
        try {
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            String namespace = root.attributeValue("namespace");
            if (namespace == null || namespace.isEmpty()) {
                throw new RuntimeException("<mapper> 标签必须存在 namespace 属性");
            }
            Class<?> mapperInterface = Resources.classForName(namespace);
            configuration.addMapper(mapperInterface);
            builderAssistant.setCurrentNamespace(namespace);

            cacheElement(root.element("cache"));
            List<Element> statElements = new ArrayList<>(root.elements("select"));
            statElements.addAll(root.elements("insert"));
            statElements.addAll(root.elements("update"));
            statElements.addAll(root.elements("delete"));
            buildStatementFromContext(statElements);
        } catch (Exception e) {
            throw new RuntimeException("解析 mybatis-mapper.xml 时出错，" +
                    "出错的 xml 文件位置是 " + resource + " 出错原因为: " + e, e);
        }
        System.err.println(resource + " 解析完毕");
    }

    private void cacheElement(Element cacheElement) {
        if (cacheElement != null) {
            String type = cacheElement.attributeValue("type", "PERPETUAL");
            String eviction = cacheElement.attributeValue("eviction", "LRU");
            Long flushInterval = Long.valueOf(cacheElement.attributeValue("flushInterval", "300000"));
            Integer size = Integer.valueOf(cacheElement.attributeValue("size", "1024"));

            Class<? extends Cache> cacheClass = typeAliasRegistry.resolveAlias(type);
            Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
            builderAssistant.useNewCache(cacheClass, evictionClass, flushInterval, size);
        }
    }

    private void buildStatementFromContext(List<Element> statElements) {
        for (Element statElement : statElements) {
            final XMLStatementBuilder statementParser =
                    new XMLStatementBuilder(configuration, builderAssistant, statElement);
            statementParser.parseStatementNode();
        }
    }
}
