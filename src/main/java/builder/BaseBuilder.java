package builder;

import session.Configuration;
import type.TypeAliasRegistry;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/6 下午10:03
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    protected <T> Class<? extends T> resolveClass(String alias) {
        if (alias == null) return null;

        try {
            return resolveAlias(alias);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }

    protected Boolean booleanValueOf(String value) {
        return value == null ? (Boolean) false : Boolean.valueOf(value);
    }

    protected Integer integerValueOf(String value) {
        return value == null ? null : Integer.valueOf(value);
    }
}
