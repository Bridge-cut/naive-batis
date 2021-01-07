package parsing;

import java.util.Properties;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午9:22
 */
public class PropertyTokenHandler implements TokenHandler {

    private static final String DEFAULT_VALUE_SEPARATOR = ":";

    private final Properties properties;

    public PropertyTokenHandler(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String handleToken(String content) {
        if (content == null || content.length() == 0) return content;

        int index = !content.contains(DEFAULT_VALUE_SEPARATOR) ? content.length()
                : content.indexOf(DEFAULT_VALUE_SEPARATOR);
        String defaultValue = index == content.length() ? "" : content.substring(index + 1);

        return properties.getProperty(content.substring(0, index), defaultValue);
    }
}
