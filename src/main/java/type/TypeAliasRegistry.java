package type;

import io.Resources;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 下午4:38
 */
public class TypeAliasRegistry {

    private final Map<String, Class<?>> typeAliases = new HashMap<>();

    public TypeAliasRegistry() {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("byte[]", Byte[].class);
        registerAlias("long[]", Long[].class);
        registerAlias("short[]", Short[].class);
        registerAlias("int[]", Integer[].class);
        registerAlias("integer[]", Integer[].class);
        registerAlias("double[]", Double[].class);
        registerAlias("float[]", Float[].class);
        registerAlias("boolean[]", Boolean[].class);

        registerAlias("_byte", byte.class);
        registerAlias("_long", long.class);
        registerAlias("_short", short.class);
        registerAlias("_int", int.class);
        registerAlias("_integer", int.class);
        registerAlias("_double", double.class);
        registerAlias("_float", float.class);
        registerAlias("_boolean", boolean.class);

        registerAlias("_byte[]", byte[].class);
        registerAlias("_long[]", long[].class);
        registerAlias("_short[]", short[].class);
        registerAlias("_int[]", int[].class);
        registerAlias("_integer[]", int[].class);
        registerAlias("_double[]", double[].class);
        registerAlias("_float[]", float[].class);
        registerAlias("_boolean[]", boolean[].class);

        registerAlias("date", Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigDecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);

        registerAlias("date[]", Date[].class);
        registerAlias("decimal[]", BigDecimal[].class);
        registerAlias("bigDecimal[]", BigDecimal[].class);
        registerAlias("biginteger[]", BigInteger[].class);
        registerAlias("object[]", Object[].class);

        registerAlias("map", Map.class);
        registerAlias("hashmap", HashMap.class);
        registerAlias("list", List.class);
        registerAlias("arraylist", ArrayList.class);
        registerAlias("collection", Collection.class);
        registerAlias("iterator", Iterator.class);

        registerAlias("ResultSet", ResultSet.class);
    }

    public void registerAlias(Class<?> clazz) {
        String alias = clazz.getSimpleName();
        Alias aliasAnnotation = clazz.getAnnotation(Alias.class);
        if (aliasAnnotation != null) {
            alias = aliasAnnotation.value();
        }

        registerAlias(alias, clazz);
    }

    public void registerAlias(String typeAlias, Class<?> clazz) {
        if (typeAlias == null) {
            throw new RuntimeException("参数别名不能为空");
        }

        String alias = typeAlias.toLowerCase(Locale.ENGLISH);
        if (typeAliases.containsKey(alias) && typeAliases.get(alias) != null
                && !typeAliases.get(alias).equals(clazz)) {
            throw new RuntimeException("别名 '" + typeAlias + "' 已经映射到类 '"
                    + typeAliases.get(alias).getName() + "'.");
        }

        typeAliases.put(alias, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> resolveAlias(String alias) {
        try {
            if (alias == null) return null;

            String key = alias.toLowerCase(Locale.ENGLISH);

            Class<T> value;
            if (typeAliases.containsKey(key)) {
                value = (Class<T>) typeAliases.get(key);
            } else {
                value = (Class<T>) Resources.classForName(alias);
            }
            return value;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("无法解析别名 '" + alias);
        }
    }

}
