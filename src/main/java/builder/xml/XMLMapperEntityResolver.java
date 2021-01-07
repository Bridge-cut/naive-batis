package builder.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.Locale;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午10:19
 */
public class XMLMapperEntityResolver implements EntityResolver {

    private static final String MYBATIS_CONFIG_SYSTEM = "mybatis-3-config.dtd";
    private static final String MYBATIS_MAPPER_SYSTEM = "mybatis-3-mapper.dtd";

    private static final String MYBATIS_CONFIG_DTD = "com/naive/builder/xml/mybatis-3-config.dtd";
    private static final String MYBATIS_MAPPER_DTD = "com/naive/builder/xml/mybatis-3-mapper.dtd";

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        try {
            if (systemId != null) {
                String lowerCaseSystemId = systemId.toLowerCase(Locale.ENGLISH);
                if (lowerCaseSystemId.contains(MYBATIS_CONFIG_SYSTEM)) {
                    return getInputSource(MYBATIS_CONFIG_DTD, publicId, systemId);
                } else if (lowerCaseSystemId.contains(MYBATIS_MAPPER_SYSTEM)) {
                    return getInputSource(MYBATIS_MAPPER_DTD, publicId, systemId);
                }
            }
            return null;
        } catch (Exception e) {
            throw new SAXException(e.toString());
        }
    }

    private InputSource getInputSource(String path, String publicId, String systemId) {
        return null;
    }
}
