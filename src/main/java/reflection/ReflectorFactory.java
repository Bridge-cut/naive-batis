package reflection;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午7:11
 */
public interface ReflectorFactory {

    Reflector findForClass(Class<?> type);

}
