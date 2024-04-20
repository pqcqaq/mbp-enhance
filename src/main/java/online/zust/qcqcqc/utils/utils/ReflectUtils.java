package online.zust.qcqcqc.utils.utils;

import java.lang.reflect.Field;

/**
 * @author qcqcqc
 * Date: 2024/4/20
 * Time: 下午10:11
 */
public class ReflectUtils {
    private ReflectUtils() {
    }

    public static Field recursiveGetField(Class<?> clazz, String fieldName) {
        Field field = null;
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return field;
    }
}
