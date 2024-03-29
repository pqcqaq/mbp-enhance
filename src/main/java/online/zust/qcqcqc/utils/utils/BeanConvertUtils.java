package online.zust.qcqcqc.utils.utils;

import online.zust.qcqcqc.utils.annotation.convert.*;
import online.zust.qcqcqc.utils.config.JsonConverter;
import online.zust.qcqcqc.utils.conventer.handler.FieldHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author qcqcqc
 */
@Component
public class BeanConvertUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanConvertUtils.class);

    private static JsonConverter jsonConverter;

    @Autowired
    public void setJsonConverter(JsonConverter jsonConverter) {
        initJsonConverter(jsonConverter);
    }

    /**
     * 初始化json转换器
     *
     * @param jsonConverter json转换器
     */
    public static void initJsonConverter(JsonConverter jsonConverter) {
        BeanConvertUtils.jsonConverter = jsonConverter;
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
            CustomConvert annotation = clazz.getAnnotation(CustomConvert.class);
            if (annotation == null) {
                return t;
            }
            // 先进行了类型转换，再进行字段扫描和处理
            return fieldConvent(entity, t, annotation.nullable());
        } catch (Exception e) {
            throw new BeanConventException(e);
        }
    }

    /**
     * 字符串转换为对象
     *
     * @param entity 字符串
     * @param clazz  目标类型
     * @param <T>    目标类型
     * @return 目标类型对象
     */
    public static <T> T objectConvent(String entity, Class<T> clazz) {
        if (entity == null) {
            return null;
        }
        return jsonConverter.fromString(entity, clazz);
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
            if (declaredField.getName().equals("serialVersionUID")) {
                continue;
            }
            if (declaredField.getAnnotation(RequireDefault.class) != null) {
                setDefaultValue(after, declaredField);
                continue;
            }
            if (declaredField.isAnnotationPresent(HandleField.class)) {
                try {
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(after);
                    HandleField annotation = declaredField.getAnnotation(HandleField.class);
                    Class<? extends FieldHandler> value = annotation.value();
                    // 从容器中获取
                    ProxyUtil.getBean(value).doConvert(before, o, declaredField);
                } catch (Exception e) {
                    log.error("字段转换失败", e);
                    throw new BeanConventException(e);
                }
                continue;
            }

            // 如果不是设值默认值，则根据注解进行赋值
            try {
                declaredField.setAccessible(true);
                // 如果字段为null,则根据注解进行赋值
                if (declaredField.get(after) == null) {
                    // 自定义注解处理
                    handleAnnotation(before, after, declaredField);
                }
                // 获取字段类型名称
                String fieldName = declaredField.getType().getName();
                // 如果字段为自定义类型,而不是java自带类型,则进行递归赋值
                if (!fieldName.startsWith("java") && !declaredField.getType().isEnum()) {
                    // 这里只会进行自定义类型的递归赋值，避免了list之类的问题
                    String name = getFieldNameFromAnnotation(declaredField, nullable);
                    // 如果没有注解或者是采用深层次查找，则直接略过，因为在上面已经处理过了
                    if (name == null || name.contains(".")) {
                        continue;
                    }
                    // 根据注解获取字段值（已经指定了数据源，则从原始对象中获取字段值，再转换到目标对象）
                    Field field = before.getClass().getDeclaredField(name);
                    field.setAccessible(true);
                    Object value = field.get(before);
                    // 如果字段值不为null,则进行递归赋值
                    if (value != null) {
                        fieldConvent(value, declaredField.get(after), nullable);
                    }
                } else {
                    // 如果是java开头的字段，则需要判断一下是不是List类型或者是数组类型
                    if (fieldName.startsWith("java.util.List")) {
                        // 检查原始数据中是否有字段
                        // 转为数组并进行递归赋值
                        List<?> list = (List<?>) declaredField.get(after);
                        // 在原始对象中查找同名的字段或者根据注解查找字段
                        String name = getFieldNameFromAnnotation(declaredField, nullable);
                        // 如果没有注解或者是采用深层次查找，则直接略过，因为在上面已经处理过了
                        if (name == null || name.contains(".")) {
                            continue;
                        }
                        Field field = before.getClass().getDeclaredField(name);
                        field.setAccessible(true);
                        List<?> listValue = (List<?>) field.get(before);
                        // 判断原始对象中的字段是否为空，如果为空则直接略过
                        if (listValue == null) {
                            // 如果走到了这一步，则一定是不包含深层指定的一个简单数组转换，如果直接是null，那么目标对象肯定需要一个空数组（目标对象不一定是null，可能存在字段重复，这里直接进行覆盖）
                            declaredField.set(after, List.of());
                            continue;
                        }
                        if (listValue.isEmpty()) {
                            continue;
                        }
                        // 原数组有数据且不为空，目标数组为null的情况，直接赋值为一个空数组
                        if (list == null) {
                            declaredField.set(after, List.of());
                            continue;
                        }
                        // 判断是不是基本类型(原始对象中)
                        Object o = listValue.get(0);
                        if (o == null || o.getClass().getName().startsWith("java")) {
                            // 如果是基本类型，直接略过，因为最开始做转换的时候肯定已经有值了（上面处理自定义注解时赋值）
                            continue;
                        }

                        // 这里不需要在判断是否为null了，因为前面已经赋值为了一个空数组
                        // 也不需要判断是否长度不相等了，因为可能原始已经赋值，这里需要覆盖掉
                        // 如果字段值不为null,则将原始对象和目标对象的List进行遍历递归赋值
                        log.debug("尝试转换List字段，从原始对象字段{}转换到目标对象字段{}", field.getName(), declaredField.getName());
                        // 如果字段值不为null,则进行递归赋值
                        Class<?> targetClass;
                        if (!list.isEmpty()) {
                            // 从第一个元素中获取
                            targetClass = list.get(0).getClass();
                        } else {
                            // 从数组的泛型信息中获取，而不是从第一个元素中获取
                            Type genericType = declaredField.getGenericType();
                            targetClass = genericType.getClass();
                        }
                        List<?> list1 = listValue.stream().map(item -> objectConvent(item, targetClass)).toList();
                        declaredField.set(after, list1);
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
     * 设置默认值
     *
     * @param after         目标对象
     * @param declaredField 字段
     * @param <A>           目标对象类型
     */
    private static <A> void setDefaultValue(A after, Field declaredField) {
        RequireDefault annotation = declaredField.getAnnotation(RequireDefault.class);
        declaredField.setAccessible(true);
        try {
            declaredField.set(after, jsonConverter.convertValue(annotation.value(), declaredField.getType()));
        } catch (Exception e) {
            if (!annotation.nullable()) {
                throw new BeanConventException(e);
            }
            try {
                declaredField.set(after, null);
            } catch (IllegalAccessException ex) {
                throw new BeanConventException(ex);
            }
            log.error("字段转换失败", e);
        }
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
                if (value == null) {
                    return null;
                }
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

    /**
     * 复制属性并返回新对象
     *
     * @param target 目标类型
     * @param source 源对象
     * @param <T>    目标类型
     * @return 目标类型对象
     */
    public static <T> T copyProperties(Class<T> target, Object... source) {
        T t = objectConvent(source[0], target);
        for (int i = 1; i < source.length; i++) {
            fieldConvent(source[i], t, true);
        }
        return t;
    }
}
