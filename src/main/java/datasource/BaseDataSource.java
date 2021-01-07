package datasource;

import io.Resources;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午10:14
 */
public abstract class BaseDataSource implements DataSource {

    private ClassLoader driverClassLoader;
    private Properties driverProperties;
    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();

    private String driver;
    private String url;
    private String username;
    private String password;

    private Boolean autoCommit;

    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    protected Connection getRealConnection() throws SQLException {
        return doGetConnection(username, password);
    }

    protected Connection getRealConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties properties = new Properties();
        if (driverProperties != null) properties.putAll(driverProperties);
        if (username != null) properties.setProperty("user", username);
        if (password != null) properties.setProperty("password", password);

        return doGetConnection(properties);
    }

    private Connection doGetConnection(Properties properties) throws SQLException {
        initializeDriver();
        Connection connection = DriverManager.getConnection(url, properties);
        configureConnection(connection);
        return connection;
    }

    private synchronized void initializeDriver() throws SQLException {
        if (!registeredDrivers.containsKey(driver)) {
            Class<?> driverType;
            try {
                if (driverClassLoader != null) {
                    driverType = Class.forName(driver, true, driverClassLoader);
                } else driverType = Resources.classForName(driver);

                Driver driverInstance = (Driver) driverType.getDeclaredConstructor().newInstance();
                DriverManager.registerDriver(driverInstance);
                registeredDrivers.put(driver, driverInstance);
            } catch (Exception e) {
                throw new SQLException("Driver 类型指定错误，具体原因是: " + e);
            }
        }
    }

    private void configureConnection(Connection connection) throws SQLException {
        if (autoCommit != null && autoCommit != connection.getAutoCommit()) {
            connection.setAutoCommit(autoCommit);
        }
    }

}
