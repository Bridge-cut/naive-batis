package parsing;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午9:04
 */
public class GenericTokenParser {

    private final String openToken;
    private final String closeToken;
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String getOpenToken() {
        return openToken;
    }

    public String getCloseToken() {
        return closeToken;
    }

    public TokenHandler getHandler() {
        return handler;
    }

    /**
     * 根据 <properties> 中配置的 <key-value> 解析 text 中可能存在的 ${} 占位符
     * 根据用户入参中配置 @Param 的解析 text 中可能存在的 #{} 占位符
     *
     * @param text 待解析字符串
     * @return 解析完成后的字符串
     */
    public String parse(String text) {
        if (text == null || text.length() == 0) return text;

        int index = 0;
        int length = text.length();
        StringBuilder returnValue = new StringBuilder();
        while (index < length) {
            int openIndex = text.indexOf(openToken, index);
            if (openIndex != -1) {
                returnValue.append(text, index, openIndex);
                index = openIndex + openToken.length();
                int closeIndex = text.indexOf(closeToken, index);
                if (closeIndex == -1) {
                    throw new RuntimeException("占位开始符 " + openToken + " 必须存在与之相对应的占位结束符 " + closeToken);
                }
                returnValue.append(handler.handleToken(text.substring(index, closeIndex)));
                index = closeIndex + closeToken.length();
            } else {
                returnValue.append(text.substring(index));
                index = length;
            }
        }

        return returnValue.toString();
    }
}
