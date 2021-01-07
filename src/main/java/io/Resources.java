package io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 上午11:08
 */
public class Resources {

    private static final ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    /**
     * 返回类路径上的资源作为 InputStream 对象
     *
     * @param resource 待寻找的资源
     * @return 资源对应的 InputStream 对象
     * @throws IOException 如果找不到或无法读取资源
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    /**
     * 返回类路径上的资源作为 InputStream 对象
     *
     * @param loader   用于获取资源的类加载器
     * @param resource 待寻找的资源
     * @return 资源对应的 InputStream 对象
     * @throws IOException 如果找不到或无法读取资源
     */
    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
        if (in == null) {
            throw new IOException("无法找到资源 " + resource);
        }
        return in;
    }

    /**
     * 返回类路径上的资源作为 Properties 对象
     *
     * @param resource 待寻找的资源
     * @return 资源对应的 Properties 对象
     * @throws IOException 如果找不到或无法读取资源
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getResourceAsStream(resource)) {
            properties.load(in);
        }
        return properties;
    }

    /**
     * 获取作为属性对象的 URL
     *
     * @param urlString - 待寻找的 URL 资源
     * @return 包含来自 URL 的数据的 Properties 对象
     * @throws IOException 如果找不到或无法读取资源
     */
    public static Properties getUrlAsProperties(String urlString) throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getUrlAsStream(urlString)) {
            properties.load(in);
        }
        return properties;
    }

    /**
     * 获取作为输入流的 URL
     *
     * @param urlString - 待寻找的 URL 资源
     * @return InputStream 其中包含来自 URL 的数据
     * @throws IOException 如果找不到或无法读取资源
     */
    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }

    /**
     * 加载类
     *
     * @param className - 要获取的类
     * @return 加载的类
     * @throws ClassNotFoundException 如果找不到或无法读取资源
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(className);
    }
}
