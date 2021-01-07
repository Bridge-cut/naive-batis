package session;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 上午10:46
 */
public interface ResultContext<T> {

    T getResultObject();

    int getResultCount();

    boolean isStopped();

    void stop();

}

