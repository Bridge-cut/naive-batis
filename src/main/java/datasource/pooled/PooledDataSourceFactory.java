package datasource.pooled;

import datasource.DataSourceFactory;
import reflection.MetaObject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午8:52
 */
public class PooledDataSourceFactory implements DataSourceFactory {

    private static final String DRIVER_PROPERTY_PREFIX = "driver.";
    private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();

    private final DataSource dataSource;

    public PooledDataSourceFactory() {
        this.dataSource = new PooledDataSource();
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void setProperties(Properties properties) {
        Properties driverProperties = new Properties();
        MetaObject metaDataSource = MetaObject.forObject(dataSource);
        for (Object key : properties.keySet()) {
            String propertyName = (String) key;
            if (propertyName.startsWith(DRIVER_PROPERTY_PREFIX)) {
                String value = properties.getProperty(propertyName);
                driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH), value);
            } else if (metaDataSource.hasSetter(propertyName)) {
                String value = (String) properties.get(propertyName);
                Object convertedValue = convertValue(metaDataSource, propertyName, value);
                metaDataSource.setValue(propertyName, convertedValue);
            } else {
                throw new RuntimeException("未知的数据源属性: " + propertyName);
            }
        }

        if (driverProperties.size() > 0) {
            metaDataSource.setValue("driverProperties", driverProperties);
        }
    }

    private Object convertValue(MetaObject metaDataSource, String propertyName, String value) {
        Object convertedValue = value;
        Class<?> targetType = metaDataSource.getSetterType(propertyName);
        if (targetType == Integer.class || targetType == int.class) {
            convertedValue = Integer.valueOf(value);
        } else if (targetType == Long.class || targetType == long.class) {
            convertedValue = Long.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            convertedValue = Boolean.valueOf(value);
        }

        return convertedValue;
    }
}
