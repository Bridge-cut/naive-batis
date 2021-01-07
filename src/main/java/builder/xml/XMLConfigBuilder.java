package builder.xml;

import builder.BaseBuilder;
import datasource.DataSourceFactory;
import io.Resources;
import mapping.Environment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import parsing.GenericTokenParser;
import parsing.PropertyTokenHandler;
import reflection.impl.DefaultReflectorFactory;
import reflection.MetaClass;
import reflection.ReflectorFactory;
import session.Configuration;
import transaction.TransactionFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:29
 */
public class XMLConfigBuilder extends BaseBuilder {

    private boolean parsed;
    private final InputStream inputStream;
    private String environment;
    private final Properties properties;
    private final SAXReader saxReader;
    private final GenericTokenParser propertyTokenParser;
    private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

    public XMLConfigBuilder(InputStream inputStream, String environment, Properties properties) {
        super(new Configuration());

        this.parsed = false;
        this.inputStream = inputStream;
        this.environment = environment;
        this.properties = properties != null ? properties : new Properties();

        this.configuration.setVariables(this.properties);
        this.saxReader = new SAXReader();
        this.propertyTokenParser = new GenericTokenParser("${", "}",
                new PropertyTokenHandler(this.properties));
    }

    public Properties getProperties() {
        return properties;
    }

    public Configuration parse() {
        if (parsed) throw new RuntimeException("mybatis 配置文件重复解析");

        parsed = true;
        parseConfiguration();
        return configuration;
    }

    /**
     * 按标签顺序逐个解析 mybatis 全局配置文件
     */
    private void parseConfiguration() {
        System.err.println("开始按标签顺序逐个解析 mybatis-config.xml");
        try {
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            propertiesElement(root.element("properties"));
            Properties properties = settingsElement(root.element("settings"));
            settingsElement(properties);
            typeAliasesElement(root.element("typeAliases"));
            environmentsElement(root.element("environments"));
            mappersElement(root.element("mappers"));
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
        System.err.println("mybatis-config.xml 解析完毕");
    }

    private void propertiesElement(Element propsElement) throws IOException {
        if (propsElement != null) {
            Properties properties = new Properties();
            // 获取 resource / url 属性值
            String resource = propsElement.attributeValue("resource");
            String url = propsElement.attributeValue("url");
            if (resource != null && url != null) {
                throw new RuntimeException("<properties> 节点既指定了 resource 属性，同时也指定了 url 属性");
            }

            // 将 resource / url 中的属性值加入 properties
            if (resource != null) {
                properties.putAll(Resources.getResourceAsProperties(resource));
            }
            if (url != null) {
                properties.putAll(Resources.getUrlAsProperties(url));
            }

            // 将 <properties> 子节点的 name:value 加入 properties
            for (Element propElement : propsElement.elements()) {
                String name = propElement.attributeValue("name");
                String value = propElement.attributeValue("value");
                if (name != null && value != null) {
                    properties.setProperty(name, value);
                }
            }

            getProperties().putAll(properties);
            configuration.getVariables().putAll(properties);
        }
    }

    private Properties settingsElement(Element settingsElement) {
        Properties properties = new Properties();
        if (settingsElement != null) {
            MetaClass metaConfig = MetaClass.forClass(configuration.getClass(), localReflectorFactory);
            for (Element setting : settingsElement.elements()) {
                String name = setting.attributeValue("name");
                String value = setting.attributeValue("value");

                if (!metaConfig.hasSetter(name)) {
                    throw new RuntimeException("<setting> 标签不存在属性 " + name + " 请检查输入是否有误");
                }
                properties.setProperty(name, value);
            }
        }

        return properties;
    }

    private void settingsElement(Properties properties) {
        configuration.setUseGeneratedKeys(booleanValueOf(properties.getProperty("useGeneratedKeys")
        ));
        configuration.setMapUnderscoreToCamelCase(booleanValueOf(properties.getProperty("mapUnderscoreToCamelCase")
        ));
        configuration.setDefaultStatementTimeout(integerValueOf(properties.getProperty("defaultStatementTimeout")
        ));
        configuration.setDefaultFetchSize(integerValueOf(properties.getProperty("defaultFetchSize")
        ));
    }

    private void typeAliasesElement(Element typesElement) {
        if (typesElement != null) {
            for (Element typeElement : typesElement.elements()) {
                String alias = typeElement.attributeValue("alias");
                String type = typeElement.attributeValue("type");

                try {
                    Class<?> clazz = Resources.classForName(type);
                    if (alias == null) {
                        typeAliasRegistry.registerAlias(clazz);
                    } else typeAliasRegistry.registerAlias(alias, clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void environmentsElement(Element ensElement) {
        if (ensElement != null) {
            if (environment == null) {
                environment = ensElement.attributeValue("default");
            }

            for (Element environment : ensElement.elements()) {
                String id = environment.attributeValue("id");
                if (this.environment.equals(id)) {
                    TransactionFactory txFactory = transactionManagerElement(environment.element("transactionManager"));
                    DataSourceFactory dataSourceFactory = dataSourceElement(environment.element("dataSource"));
                    DataSource dataSource = dataSourceFactory.getDataSource();
                    Environment _environment = new Environment(id, txFactory, dataSource);
                    configuration.setEnvironment(_environment);
                }
            }
        }
    }

    private TransactionFactory transactionManagerElement(Element txManagerElement) {
        if (txManagerElement != null) {
            String type = txManagerElement.attributeValue("type");
            try {
                TransactionFactory txFactory = (TransactionFactory) resolveClass(type)
                        .getDeclaredConstructor().newInstance();

                Properties properties = new Properties();
                for (Element property : txManagerElement.elements()) {
                    String name = property.attributeValue("name");
                    String value = property.attributeValue("value");

                    if (value == null) value = "true";
                    properties.setProperty(name, value);
                }
                txFactory.setProperties(properties);

                return txFactory;
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException("<environment> 标签需要声明 <transactionManager>");
    }

    private DataSourceFactory dataSourceElement(Element dsElement) {
        if (dsElement != null) {
            String type = dsElement.attributeValue("type");
            try {
                DataSourceFactory dataSourceFactory = (DataSourceFactory) resolveClass(type)
                        .getDeclaredConstructor().newInstance();

                Properties properties = new Properties();
                for (Element property : dsElement.elements()) {
                    String name = property.attributeValue("name");
                    String value = property.attributeValue("value");

                    if (name != null && value != null) {
                        properties.setProperty(name, propertyTokenParser.parse(value));
                    }
                }
                dataSourceFactory.setProperties(properties);

                return dataSourceFactory;
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException("<environment> 标签需要声明 <dataSource>");
    }

    private void mappersElement(Element mappersElement) throws IOException {
        System.err.println("开始逐个解析 mybatis-mapper.xml");
        if (mappersElement != null) {
            for (Element mapper : mappersElement.elements()) {
                String resource = mapper.attributeValue("resource");
                String url = mapper.attributeValue("url");
                String mapperClass = mapper.attributeValue("class");
                if (resource != null && url == null && mapperClass == null) {
                    InputStream inputStream = Resources.getResourceAsStream(resource);
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
                    mapperParser.parse();
                } else if (resource == null && url != null && mapperClass == null) {
                    InputStream inputStream = Resources.getUrlAsStream(url);
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url);
                    mapperParser.parse();
                } else if (resource == null && url == null && mapperClass != null) {
                    String xmlResource = mapperClass.replace('.', '/') + ".xml";
                    InputStream inputStream = Resources.getResourceAsStream(xmlResource);
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, xmlResource);
                    mapperParser.parse();
                } else {
                    throw new RuntimeException("<mapper> 标签只能指定 url resource 或 class，但不能超过一个");
                }
            }
        }
        System.err.println("已经解析完所有 mybatis-mapper.xml");
    }
}
