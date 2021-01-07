package reflection;

import java.lang.reflect.Method;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/12 下午8:24
 */
public interface ParameterNameDiscoverer {

    String[] getParameterNames(Method method);

}