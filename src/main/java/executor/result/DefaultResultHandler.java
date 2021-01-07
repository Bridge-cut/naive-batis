package executor.result;

import session.ResultContext;
import session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 上午10:49
 */
public class DefaultResultHandler implements ResultHandler<Object> {

    private final List<Object> list;

    public DefaultResultHandler() {
        list = new ArrayList<>();
    }

    public List<Object> getList() {
        return list;
    }

    @Override
    public void handleResult(ResultContext<?> resultContext) {
        list.add(resultContext.getResultObject());
    }
}
