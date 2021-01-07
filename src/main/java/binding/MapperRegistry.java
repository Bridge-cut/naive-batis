package binding;

import session.Configuration;
import session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/11 上午11:23
 */
public class MapperRegistry {

    private final Configuration configuration;

    // <key=mapper 接口, value=该 mapper 接口对应的代理类的生成工厂>
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    /**
     * 注册 type 接口对应的代理类对应的工厂
     *
     * @param type 待注册代理类的对应的工厂接口
     */
    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new RuntimeException("不可重复生成该接口对应的代理类对应的工厂");
            }
            boolean loadCompleted = false;
            try {
                // 注册该接口对应的代理类对应的工厂
                knownMappers.put(type, new MapperProxyFactory<>(type));
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }

    /**
     * 获取接口 type 对应的代理类对象
     *
     * @param type 待获取代理类对象的接口
     * @param sqlSession 此次会话
     * @return 获取到的 type 接口的代理类
     */
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("接口 " + type + " 未注册其对应代理类的生成工厂");
        }

        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("获取接口 " + type + " 的代理类失败，失败原因在于: " + e, e);
        }
    }
}
