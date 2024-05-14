package online.zust.qcqcqc.utils.utils;

import com.baomidou.mybatisplus.annotation.TableId;

import java.lang.annotation.Annotation;
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

    public static Field recursiveGetField(Class<?> clazz, Class<? extends Annotation> annotation) {
        Field field = null;
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(annotation)) {
                    field = declaredField;
                    break;
                }
            }
            if (field != null) {
                break;
            }
            clazz = clazz.getSuperclass();
        }
        return field;
    }

    public static Field recursiveGetFieldWithAnnotation(Class<?> entityClass, Class<? extends Annotation> tableIdClass) {
        Field field = null;
        while (entityClass != null) {
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(tableIdClass)) {
                    field = declaredField;
                    break;
                }
            }
            if (field != null) {
                break;
            }
            entityClass = entityClass.getSuperclass();
        }
        return field;
    }
}
