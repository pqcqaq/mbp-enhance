package online.zust.services.utils;

import online.zust.services.annotation.convent.CustomConvent;
import online.zust.services.annotation.convent.FromField;
import online.zust.services.annotation.convent.SourceField;
import online.zust.services.config.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author qcqcqc
 */
@Component
public class BeanConventUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanConventUtils.class);

    private static JsonConverter jsonConverter;

    @Autowired
    public void setJsonConverter(JsonConverter jsonConverter) {
        BeanConventUtils.jsonConverter = jsonConverter;
    }

    /**
     * 对象转换异常
     */
    public final static class BeanConventException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 3211204477960618903L;

        public BeanConventException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * 对象转换
     *
     * @param entity 实体
     * @param clazz  目标类型
     * @param <T>    目标类型
     * @param <M>    实体类型
     * @return 目标类型对象
     */
    public static <T, M> T objectConvent(M entity, Class<T> clazz) {
        if (entity == null) {
            return null;
        }
        try {
            T t = jsonConverter.convertValue(entity, clazz);
            CustomConvent annotation = clazz.getAnnotation(CustomConvent.class);
            if (annotation == null) {
                return t;
            }
            return fieldConvent(entity, t, annotation.nullable());
        } catch (Exception e) {
            throw new BeanConventException(e);
        }
    }

    /**
     * 字段转换
     *
     * @param before 源对象
     * @param after  目标对象
     * @param <A>    目标对象类型
     * @param <B>    源对象类型
     * @return 目标对象
     */
    public static <A, B> A fieldConvent(B before, A after, boolean nullable) {
        if (after == null) {
            return null;
        }
        Field[] declaredFields = after.getClass().getDeclaredFields();
        // 获取有指定注解并且为null的字段
        for (Field declaredField : declaredFields) {
            try {
                declaredField.setAccessible(true);
                // 如果字段为null,则根据注解进行赋值
                if (declaredField.get(after) == null) {
                    // 自定义注解处理
                    handleAnnotation(before, after, declaredField);
                }
                // 如果字段为自定义类型,而不是java自带类型,则进行递归赋值
                if (!declaredField.getType().getName().startsWith("java") && !declaredField.getType().isEnum()) {
                    String name = getFieldNameFromAnnotation(declaredField, nullable);
                    if (name == null) {
                        continue;
                    }
                    // 根据注解获取字段值
                    Field field = before.getClass().getDeclaredField(name);
                    field.setAccessible(true);
                    Object value = field.get(before);
                    // 如果字段值不为null,则进行递归赋值
                    if (value != null) {
                        fieldConvent(value, declaredField.get(after), nullable);
                    }
                }
            } catch (Exception e) {
                log.error("字段转换失败", e);
                throw new BeanConventException(e);
            }
        }
        return after;
    }

    /**
     * 根据注解获取字段名
     *
     * @param declaredField 字段
     * @param nullable      是否允许字段为null
     * @return 字段名
     */
    private static String getFieldNameFromAnnotation(Field declaredField, boolean nullable) {
        String name;
        if (declaredField.getAnnotation(SourceField.class) != null) {
            name = declaredField.getAnnotation(SourceField.class).name();
        } else {
            if (declaredField.getAnnotation(FromField.class) != null) {
                name = declaredField.getAnnotation(FromField.class).fieldPath();
            } else {
                if (!nullable) {
                    throw new BeanConventException(new RuntimeException("存在无法处理的字段" + declaredField.getName() + "，可能未添加注解"));
                }
                log.warn("存在未处理的字段{}，可能未添加注解", declaredField.getName());
                return null;
            }
        }
        return name;
    }

    /**
     * 处理自定义注解
     *
     * @param before        源对象
     * @param after         目标对象
     * @param declaredField 字段
     * @param <A>           目标对象类型
     * @param <B>           源对象类型
     */
    private static <A, B> void handleAnnotation(B before, A after, Field declaredField) {
        // 自定义注解处理
        checkSourceFieldAnnotation(before, after, declaredField);
        checkFromFieldAnnotation(before, after, declaredField);
    }

    /**
     * 检查SourceField注解
     *
     * @param before        源对象
     * @param after         目标对象
     * @param declaredField 字段
     * @param <A>           目标对象类型
     * @param <B>           源对象类型
     */
    private static <A, B> void checkSourceFieldAnnotation(B before, A after, Field declaredField) {
        SourceField annotation = declaredField.getAnnotation(SourceField.class);
        try {
            if (annotation != null) {
                String fieldName = annotation.name();
                Field field = before.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(before);
                setValue(after, declaredField, value);
            }
        } catch (Exception e) {
            if (annotation.nullable()) {
                log.warn("SourceField字段转换失败", e);
                return;
            }
            log.error("SourceField字段转换失败", e);
            throw new BeanConventException(e);
        }
    }

    /**
     * 检查FromField注解
     *
     * @param before        源对象
     * @param after         目标对象
     * @param declaredField 字段
     * @param <A>           目标对象类型
     * @param <B>           源对象类型
     */
    private static <A, B> void checkFromFieldAnnotation(B before, A after, Field declaredField) {
        FromField annotation = declaredField.getAnnotation(FromField.class);
        try {
            if (annotation != null) {
                String fieldName = annotation.fieldPath();
                Object fieldValue = getFieldValueByPath(before, fieldName);
                setValue(after, declaredField, fieldValue);
            }
        } catch (Exception e) {
            if (annotation.nullable()) {
                log.warn("FromField字段转换失败", e);
                return;
            }
            log.error("FromField字段转换失败", e);
            throw new BeanConventException(e);
        }
    }

    /**
     * 设置字段值
     *
     * @param after         目标对象
     * @param declaredField 字段
     * @param fieldValue    字段值
     * @param <A>           目标对象类型
     */
    private static <A> void setValue(A after, Field declaredField, Object fieldValue) {
        try {
            declaredField.setAccessible(true);
            Object o = jsonConverter.convertValue(fieldValue, declaredField.getType());
            declaredField.set(after, o);
        } catch (Exception e) {
            log.error("字段设置失败", e);
            throw new BeanConventException(e);
        }
    }

    /**
     * 根据字段路径获取字段值
     *
     * @param before    源对象
     * @param fieldName 字段路径
     * @param <B>       源对象类型
     * @return 字段值
     */
    private static <B> Object getFieldValueByPath(B before, String fieldName) {
        String[] split = fieldName.split("\\.");
        Field field;
        Object value = before;
        for (String s : split) {
            try {
                // 如果是递归进来的，存在可能是List<genericType>类型，还需要进行判断
                if (s.startsWith("$")) {
                    log.debug("已指定从数组中获取字段路径为{}", s);
                    s = s.substring(1);
                }
                if (value instanceof List<?>) {
                    // 继续递归，完成List<genericType>类型的字段获取
                    value = getObjectInList((List<?>) value, s);
                } else {
                    field = value.getClass().getDeclaredField(s);
                    field.setAccessible(true);
                    value = field.get(value);
                }
            } catch (Exception e) {
                log.error("获取指定字段失败", e);
                throw new BeanConventException(e);
            }
        }
        return value;
    }

    /**
     * 获取List中的字段
     *
     * @param list          List
     * @param trueFieldName 字段名
     * @return 字段值
     */
    private static List<?> getObjectInList(List<?> list, String trueFieldName) {
        List<?> value;
        value = list.stream().map(o -> {
            if (o == null) {
                return null;
            }
            // 如果还是List<genericType>类型,则获取genericType的字段,达到最后结果为List<List<List....>>的情况
            if (o instanceof List<?> inlineLists) {
                return inlineLists.stream().map(inlineList -> getFieldValueByPath(inlineList, trueFieldName)).toList();
            }
            return getFieldValueByPath(o, trueFieldName);
        }).toList();
        return value;
    }
}
