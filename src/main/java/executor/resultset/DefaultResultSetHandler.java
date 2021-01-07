package executor.resultset;

import executor.Executor;
import mapping.BoundSql;
import mapping.MappedStatement;
import reflection.MetaObject;
import reflection.ReflectorFactory;
import session.Configuration;
import session.ResultHandler;
import session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午8:11
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    private static final String CAMEl_CASE_MARK = "_";

    private final Executor executor;
    private final Configuration configuration;
    private final MappedStatement mappedStatement;
    private final RowBounds rowBounds;
    private final ResultHandler<?> resultHandler;
    private final BoundSql boundSql;
    private final ReflectorFactory reflectorFactory;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement,
                                   ResultHandler<?> resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
        this.mappedStatement = mappedStatement;
        this.rowBounds = rowBounds;
        this.boundSql = boundSql;
        this.reflectorFactory = configuration.getReflectorFactory();
        this.resultHandler = resultHandler;
    }

    public Executor getExecutor() {
        return executor;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MappedStatement getMappedStatement() {
        return mappedStatement;
    }

    public RowBounds getRowBounds() {
        return rowBounds;
    }

    public ResultHandler<?> getResultHandler() {
        return resultHandler;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        List<Object> result = new ArrayList<>();
        Class<?> componentType = mappedStatement.getResultTypeClass();
        ResultSet resultSet = ((PreparedStatement) stmt).executeQuery();

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (resultSet.next()) {
            try {
                MetaObject componentInstance = MetaObject.forObject(componentType.getDeclaredConstructor().newInstance());
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    StringBuilder propertyName = new StringBuilder(columnName);
                    if (configuration.isMapUnderscoreToCamelCase()) {
                        int index = propertyName.indexOf(CAMEl_CASE_MARK);
                        while (index != -1) {
                            propertyName.deleteCharAt(index);
                            if (index < propertyName.length()) {
                                propertyName.setCharAt(index,
                                        String.valueOf(propertyName.charAt(index)).toUpperCase().charAt(0));
                            }

                            index = propertyName.indexOf(CAMEl_CASE_MARK);
                        }
                    }

                    if (componentInstance.hasSetter(propertyName.toString())) {
                        Class<?> propertyType = componentInstance.getSetterType(propertyName.toString());
                        setObjectValue(propertyType, propertyName.toString(), componentInstance,
                                columnName, resultSet);
                    } else {
                        throw new RuntimeException(componentType + " 中不存在与数据库列名 "
                                + columnName + " 相对应的属性名");
                    }
                }

                result.add(componentInstance.getOriginalObject());
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void setObjectValue(Class<?> propertyType, String propertyName, MetaObject componentInstance,
                                String columnName, ResultSet resultSet) throws SQLException {
        if (Float.class.equals(propertyType)) componentInstance.setValue(propertyName, resultSet.getFloat(columnName));
        if (Double.class.equals(propertyType)) componentInstance.setValue(propertyName, resultSet.getDouble(columnName));
        if (Long.class.equals(propertyType)) componentInstance.setValue(propertyName, resultSet.getDouble(columnName));
        if (Integer.class.equals(propertyType)) componentInstance.setValue(propertyName, resultSet.getInt(columnName));
        if (Boolean.class.equals(propertyType)) componentInstance.setValue(propertyName, resultSet.getBoolean(columnName));
        if (Short.class.equals(propertyType)) componentInstance.setValue(propertyName, resultSet.getShort(columnName));
        if (String.class.equals(propertyType)) componentInstance.setValue(propertyName, resultSet.getString(columnName));
    }
}
