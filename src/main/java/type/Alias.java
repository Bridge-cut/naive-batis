package type;

import java.lang.annotation.*;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午5:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Alias {

    // 指定的别名
    String value();
}
