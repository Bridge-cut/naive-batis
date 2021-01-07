package plugin;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午4:43
 */
public interface Interceptor {

    Object intercept(Invocation invocation) throws Throwable;

    default Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
