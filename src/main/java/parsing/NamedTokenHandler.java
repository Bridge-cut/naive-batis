package parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/17 下午4:09
 */
public class NamedTokenHandler implements TokenHandler {

    private final static String DEFAULT_VALUE = "?";
    private final List<String> namedParameterIndex = new ArrayList<>();

    public List<String> getNamedParameterIndex() {
        return namedParameterIndex;
    }

    @Override
    public String handleToken(String content) {
        if (content == null || content.length() == 0) return content;

        namedParameterIndex.add(content);
        return DEFAULT_VALUE;
    }
}
