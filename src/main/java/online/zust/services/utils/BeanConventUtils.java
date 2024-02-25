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

    public final static class BeanConventException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 3211204477960618903L;

        public BeanConventException(Throwable cause) {
            super(cause);
        }
    }

    public static <T, M> T objectConvent(M entity, Class<T> clazz) {
        if (entity == null) {
            return null;
        }
        try {
            T t = jsonConverter.convertValue(entity, clazz);
            if (clazz.getAnnotation(CustomConvent.class) == null) {
                return t;
            }
            return fieldConvent(entity, t);
        } catch (Exception e) {
            throw new BeanConventException(e);
        }
    }

    public static <A, B> A fieldConvent(B before, A after) {
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
                    checkSourceFieldAnnotation(before, after, declaredField);
                    checkFromFieldAnnotation(before, after, declaredField);
                }
                if (!declaredField.getType().getName().startsWith("java") && !declaredField.getType().isEnum()) {
                    String name;
                    if (declaredField.getAnnotation(SourceField.class) != null) {
                        name = declaredField.getAnnotation(SourceField.class).name();
                    } else {
                        name = declaredField.getName();
                    }
                    Field field = before.getClass().getDeclaredField(name);
                    field.setAccessible(true);
                    Object value = field.get(before);
                    if (value != null) {
                        fieldConvent(value, declaredField.get(after));
                    }
                }
            } catch (Exception e) {
                log.error("字段转换失败", e);
                throw new BeanConventException(e);
            }
        }
        return after;
    }

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

    private static <B> Object getFieldValueByPath(B before, String fieldName) {
        String[] split = fieldName.split("\\.");
        Field field;
        Object value = before;
        boolean valueIsList = false;
        for (String s : split) {
            try {
                if (s.startsWith("$")) {
                    // value is List<genericType>
                    String trueFieldName = s.substring(1);
                    if (value instanceof List<?> list) {
                        if (list.isEmpty()) {
                            return null;
                        }
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
                        valueIsList = true;
                    } else {
                        throw new BeanConventException(new RuntimeException("字段类型不在List中"));
                    }
                } else {
                    if (valueIsList) {
                        List<?> list = (List<?>) value;
                        value = list.stream().map(o -> getFieldValueByPath(o, s)).toList();
                    } else {
                        field = value.getClass().getDeclaredField(s);
                        field.setAccessible(true);
                        value = field.get(value);
                    }
                }
            } catch (Exception e) {
                log.error("获取指定字段失败", e);
                throw new BeanConventException(e);
            }
        }
        return value;
    }
}
