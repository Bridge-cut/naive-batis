package plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午4:43
 */
public class InterceptorChain {

    private final List<Interceptor> interceptors = new ArrayList<>();

    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

}
