package datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午9:41
 */
public interface DataSourceFactory {

    void setProperties(Properties props);

    DataSource getDataSource();

}
