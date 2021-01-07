package reflection.impl;

import reflection.Reflector;
import reflection.ReflectorFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午7:11
 */
public class DefaultReflectorFactory implements ReflectorFactory {

    private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

    @Override
    public Reflector findForClass(Class<?> type) {
        return reflectorMap.computeIfAbsent(type, Reflector::new);
    }

}
