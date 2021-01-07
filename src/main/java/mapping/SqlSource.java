package mapping;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午7:50
 */
public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);

}
