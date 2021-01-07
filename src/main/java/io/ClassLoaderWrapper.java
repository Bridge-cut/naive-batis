package io;

import java.io.InputStream;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 上午11:08
 */
public class ClassLoaderWrapper {

    private ClassLoader defaultClassLoader;
    private final ClassLoader systemClassLoader;

    public ClassLoaderWrapper() {
        systemClassLoader = ClassLoader.getSystemClassLoader();
    }

    public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    private InputStream getResourceAsStream(String resource, ClassLoader[] classLoaders) {
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                // 尝试通过绝对路径解析资源
                InputStream inputStream = classLoader.getResourceAsStream(resource);

                if (inputStream == null) {
                    // 尝试通过类路径解析资源
                    inputStream = classLoader.getResourceAsStream("/" + resource);
                }
                if (inputStream != null) {
                    return inputStream;
                }
            }
        }

        return null;
    }

    public Class<?> classForName(String className) throws ClassNotFoundException {
        return classForName(className, getClassLoaders(null));
    }

    private Class<?> classForName(String className, ClassLoader[] classLoaders) throws ClassNotFoundException {
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                try {
                    return Class.forName(className, true, classLoader);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new ClassNotFoundException("无法加载类 " + className);
    }

    private ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[] {
                classLoader,
                defaultClassLoader,
                Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(),
                systemClassLoader
        };
    }

}
