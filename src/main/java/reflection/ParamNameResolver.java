package reflection;

import annotations.Params;
import session.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 上午11:11
 */
public class ParamNameResolver {

    public static final String GENERIC_NAME_PREFIX = "param";

    private final Method method;
    private final Configuration configuration;

    public ParamNameResolver(Configuration configuration, Method method) {
        this.configuration = configuration;
        this.method = method;
    }

    public Object getNamedParams(Object[] args) {
        if (args == null || args.length == 0) return null;

        Map<String, Object> result = new HashMap<>();
        Params params = method.getAnnotation(Params.class);
        if (params != null) {
            String[] parameters = params.parameters();
            for (int i = 0; i < args.length; i++) {
                String key = i < parameters.length ? parameters[i] : GENERIC_NAME_PREFIX + (i + 1);
                result.put(key, args[i]);
            }
        }
        for (int i = 0; i < args.length; i++) {
            result.putIfAbsent(GENERIC_NAME_PREFIX + (i + 1), args[i]);
        }

        return result;
    }
}
