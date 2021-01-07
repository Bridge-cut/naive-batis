package reflection;

import java.lang.reflect.Method;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午7:08
 */
public class MetaClass {

    private final ReflectorFactory reflectorFactory;
    private final Reflector reflector;

    private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
        this.reflector = reflectorFactory.findForClass(type);
    }

    public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
        return new MetaClass(type, reflectorFactory);
    }

    public boolean hasSetter(String propertyName) {
        return reflector.hasSetter(propertyName);
    }

    public Method getSetterMethod(String propertyName) {
        return reflector.getSetterMethod(propertyName);
    }

    public Method getGetterMethod(String propertyName) {
        return reflector.getGetterMethod(propertyName);
    }

    public Class<?> getSetterType(String propertyName) {
        return reflector.getSetterType(propertyName);
    }
}
