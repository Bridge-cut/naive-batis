package binding;

import mapping.MappedStatement;
import mapping.SqlCommandType;
import reflection.ParamNameResolver;
import session.Configuration;
import session.RowBounds;
import session.SqlSession;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 上午10:44
 */
public class MapperMethod {

    private final Method method;
    private final SqlCommand command;
    private final ParamNameResolver paramNameResolver;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.method = method;
        this.command = new SqlCommand(configuration, mapperInterface, method);
        this.paramNameResolver = new ParamNameResolver(configuration, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        switch (command.getType()) {
            case INSERT: {
                Object param = paramNameResolver.getNamedParams(args);
                result = rowCountResult(sqlSession.insert(command.getName(), param));
                break;
            }
            case UPDATE: {
                Object param = paramNameResolver.getNamedParams(args);
                result = rowCountResult(sqlSession.update(command.getName(), param));
                break;
            }
            case DELETE: {
                Object param = paramNameResolver.getNamedParams(args);
                result = rowCountResult(sqlSession.delete(command.getName(), param));
                break;
            }
            case SELECT: {
                Class<?> returnType = method.getReturnType();
                if (returnType.isArray() || List.class.isAssignableFrom(returnType)) {
                    result = executeForMany(sqlSession, args);
                } else {
                    Object param = paramNameResolver.getNamedParams(args);
                    result = sqlSession.selectOne(command.getName(), param);
                }
                break;
            }
            default:
                throw new RuntimeException(command.getName() + " 找不到与之对应的 sql 命令类型");
        }

        return result;
    }

    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        List<E> result;
        Object param = paramNameResolver.getNamedParams(args);
        List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
        if (parameterTypes.contains(RowBounds.class)) {
            RowBounds rowBounds = (RowBounds) args[parameterTypes.indexOf(RowBounds.class)];
            result = sqlSession.selectList(command.getName(), param, rowBounds);
        } else {
            result = sqlSession.selectList(command.getName(), param);
        }

        return method.getReturnType().isArray() ? convertToArray(result) : result;
    }

    @SuppressWarnings("unchecked")
    private <E> Object convertToArray(List<E> list) {
        Class<?> arrayComponentType = method.getReturnType().getComponentType();
        Object array = Array.newInstance(arrayComponentType, list.size());
        if (arrayComponentType.isPrimitive()) {
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            return array;
        } else {
            return list.toArray((E[]) array);
        }
    }

    private Object rowCountResult(int rowCount) {
        final Object result;
        final Class<?> returnType = method.getReturnType();
        if (void.class.equals(returnType)) {
            result = null;
        } else if (Integer.class.equals(returnType) || Integer.TYPE.equals(returnType)) {
            result = rowCount;
        } else if (Long.class.equals(returnType) || Long.TYPE.equals(returnType)) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
            result = rowCount > 0;
        } else {
            throw new RuntimeException("接口方法 " + command.getName()
                    + " 存在不支持的返回值类型: " + returnType);
        }

        return result;
    }

    public static class SqlCommand {

        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            final String namespace = mapperInterface.getName();
            final String id = method.getName();
            MappedStatement mappedStatement = configuration.getMappedStatement(namespace + "." + id);

            name = namespace + "." + mappedStatement.getId();
            type = mappedStatement.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }
}
