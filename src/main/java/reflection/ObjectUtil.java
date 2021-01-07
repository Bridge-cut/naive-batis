package reflection;

import java.util.Arrays;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/15 上午8:42
 */
public class ObjectUtil {

    public static int hashCode(Object object) {
        if (object == null) return 0;
        
        Class<?> clazz = object.getClass();
        if (!clazz.isArray()) return object.hashCode();
        
        Class<?> componentType = clazz.getComponentType();
        if (long.class.equals(componentType)) {
            return Arrays.hashCode((long[]) object);
        } else if (int.class.equals(componentType)) {
            return Arrays.hashCode((int[]) object);
        } else if (short.class.equals(componentType)) {
            return Arrays.hashCode((short[]) object);
        } else if (char.class.equals(componentType)) {
            return Arrays.hashCode((char[]) object);
        } else if (byte.class.equals(componentType)) {
            return Arrays.hashCode((byte[]) object);
        } else if (boolean.class.equals(componentType)) {
            return Arrays.hashCode((boolean[]) object);
        } else if (float.class.equals(componentType)) {
            return Arrays.hashCode((float[]) object);
        } else if (double.class.equals(componentType)) {
            return Arrays.hashCode((double[]) object);
        } else {
            return Arrays.hashCode((Object[]) object);
        }
    }

    public static boolean equals(Object thisObject, Object thatObject) {
        if (thisObject == null) {
            return thatObject == null;
        } else if (thatObject == null) {
            return false;
        }
        if (thisObject == thatObject) return true;
        
        final Class<?> clazz = thisObject.getClass();
        if (!clazz.equals(thatObject.getClass())) return false;
        if (!clazz.isArray()) return thisObject.equals(thatObject);

        final Class<?> componentType = clazz.getComponentType();
        if (long.class.equals(componentType)) {
            return Arrays.equals((long[]) thisObject, (long[]) thatObject);
        } else if (int.class.equals(componentType)) {
            return Arrays.equals((int[]) thisObject, (int[]) thatObject);
        } else if (short.class.equals(componentType)) {
            return Arrays.equals((short[]) thisObject, (short[]) thatObject);
        } else if (char.class.equals(componentType)) {
            return Arrays.equals((char[]) thisObject, (char[]) thatObject);
        } else if (byte.class.equals(componentType)) {
            return Arrays.equals((byte[]) thisObject, (byte[]) thatObject);
        } else if (boolean.class.equals(componentType)) {
            return Arrays.equals((boolean[]) thisObject, (boolean[]) thatObject);
        } else if (float.class.equals(componentType)) {
            return Arrays.equals((float[]) thisObject, (float[]) thatObject);
        } else if (double.class.equals(componentType)) {
            return Arrays.equals((double[]) thisObject, (double[]) thatObject);
        } else {
            return Arrays.equals((Object[]) thisObject, (Object[]) thatObject);
        }
    }
}
