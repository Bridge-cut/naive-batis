package executor.result;

import session.ResultContext;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 上午10:47
 */
public class DefaultResultContext<T> implements ResultContext<T> {

    private T resultObject;
    private int resultCount;
    private boolean stopped;

    public DefaultResultContext() {
        resultObject = null;
        resultCount = 0;
        stopped = false;
    }

    public void nextResultObject(T resultObject) {
        resultCount++;
        this.resultObject = resultObject;
    }

    @Override
    public T getResultObject() {
        return resultObject;
    }

    public void setResultObject(T resultObject) {
        this.resultObject = resultObject;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

}