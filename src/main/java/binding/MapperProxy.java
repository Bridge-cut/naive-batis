package binding;

import session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/11 上午11:46
 */
public class MapperProxy<T> implements InvocationHandler {

    // 该 MapperProxy 代理的 mapper 接口
    private final Class<T> mapperInterface;

    // 对 type 接口的方法均交由 sqlSession 执行 selectOne selectList insert delete update
    private final SqlSession sqlSession;

    // 对 Method 的调用器的缓存
    private final Map<Method, MapperMethodInvoker> methodCache;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface,
                       Map<Method, MapperMethodInvoker> methodCache) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
        } catch (Throwable t) {
            throw new RuntimeException("无法调用 " + method + " 方法，原因是: " + t);
        }
    }

    private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
        try {
            MapperMethodInvoker invoker = methodCache.get(method);
            if (invoker != null) return invoker;

            return methodCache.computeIfAbsent(method, value -> new DefaultMethodInvoker(new MapperMethod(mapperInterface,
                    method, sqlSession.getConfiguration())));
        } catch (RuntimeException re) {
            Throwable cause = re.getCause();
            throw cause == null ? re : cause;
        }
    }

    interface MapperMethodInvoker {
        Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable;
    }

    private static class DefaultMethodInvoker implements MapperMethodInvoker {
        private final MapperMethod mapperMethod;

        public DefaultMethodInvoker(MapperMethod mapperMethod) {
            super();
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
            return mapperMethod.execute(sqlSession, args);
        }
    }

}
