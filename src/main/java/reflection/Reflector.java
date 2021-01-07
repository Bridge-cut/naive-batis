package reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午7:11
 */
public class Reflector {

    private final Class<?> type;
    private final String[] readablePropertyNames;
    private final String[] writablePropertyNames;
    private final Map<String, Method> setMethods = new HashMap<>();
    private final Map<String, Method> getMethods = new HashMap<>();
    private final Map<String, Class<?>> setTypes = new HashMap<>();
    private final Map<String, Class<?>> getTypes = new HashMap<>();

    public Reflector(Class<?> clazz) {
        this.type = clazz;
        addSetMethods(clazz);
        addGetMethods(clazz);
        readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        writablePropertyNames = setMethods.keySet().toArray(new String[0]);
    }

    private void addGetMethods(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("get")) {
                getMethods.put(method.getName().substring(3).toUpperCase(), method);
                getTypes.put(method.getName().substring(3).toUpperCase(), method.getReturnType());
            } else if (method.getName().startsWith("is")) {
                getMethods.put(method.getName().substring(2).toUpperCase(), method);
                getTypes.put(method.getName().substring(2).toUpperCase(), method.getReturnType());
            }
        }
    }

    private void addSetMethods(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                setMethods.put(method.getName().substring(3).toUpperCase(), method);
                setTypes.put(method.getName().substring(3).toUpperCase(), method.getParameterTypes()[0]);
            }
        }
    }

    public boolean hasSetter(String propertyName) {
        return setMethods.containsKey(propertyName.toUpperCase());
    }

    public boolean hasGetter(String propertyName) {
        return getMethods.containsKey(propertyName.toUpperCase());
    }

    public Method getSetterMethod(String propertyName) {
        Method method = setMethods.get(propertyName.toUpperCase());
        if (method == null) {
            throw new RuntimeException(propertyName + " 的 set" +
                    propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1) + " 方法不存在");
        }

        return method;
    }

    public Method getGetterMethod(String propertyName) {
        Method method = getMethods.get(propertyName.toUpperCase());
        if (method == null) {
            throw new RuntimeException(propertyName + " 的 get" +
                    propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1) + " 方法不存在");
        }

        return method;
    }

    public Class<?> getSetterType(String propertyName) {
        Class<?> type = setTypes.get(propertyName.toUpperCase());
        if (type == null) {
            throw new RuntimeException("未找到属性 " + propertyName + " 的类型");
        }

        return type;
    }
}
