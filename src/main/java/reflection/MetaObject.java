package reflection;

import reflection.impl.DefaultReflectorFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午7:37
 */
public class MetaObject {

    private final Object originalObject;
    private final MetaClass metaClass;

    private MetaObject(Object originalObject) {
        this.originalObject = originalObject;
        this.metaClass = MetaClass.forClass(originalObject.getClass(), new DefaultReflectorFactory());
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public MetaClass getMetaClass() {
        return metaClass;
    }

    public static MetaObject forObject(Object originalObject) {
        return new MetaObject(originalObject);
    }

    public void setValue(String propertyName, Object propertyValue) {
        Method method = metaClass.getSetterMethod(propertyName);
        try {
            method.invoke(originalObject, propertyValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean hasSetter(String propertyName) {
        return metaClass.hasSetter(propertyName);
    }

    public Class<?> getSetterType(String propertyName) {
        return metaClass.getSetterType(propertyName);
    }
}
