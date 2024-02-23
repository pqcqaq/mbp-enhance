package online.zust.services.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import online.zust.services.annotation.convent.SourceField;
import online.zust.services.config.ConventConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author qcqcqc
 */
@Component
public class BeanConventUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanConventUtils.class);

    public static ObjectMapper OBJECT_MAPPER;

    @Autowired
    public BeanConventUtils(Map<String, ConventConfig> conventConfigMaps) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        conventConfigMaps.forEach((k, v) -> {
            log.info("加载转换模块: {}", k);
            OBJECT_MAPPER.registerModule(v.getConventionModule());
        });
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
            T t = OBJECT_MAPPER.convertValue(entity, clazz);
            return fieldConvent(entity, t);
        } catch (Exception e) {
            throw new BeanConventException(e);
        }
    }

    public static <A, B> A fieldConvent(B before, A after) {
        Field[] declaredFields = after.getClass().getDeclaredFields();
        // 获取有指定注解并且为null的字段
        for (Field declaredField : declaredFields) {
            try {
                declaredField.setAccessible(true);
                if (declaredField.get(after) == null) {
                    SourceField annotation = declaredField.getAnnotation(SourceField.class);
                    if (annotation != null) {
                        String fieldName = annotation.name();
                        Field field = before.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = field.get(before);
                        String valueAsString = OBJECT_MAPPER.writeValueAsString(value);
                        Class<?> type = declaredField.getType();
                        Object o = OBJECT_MAPPER.readValue(valueAsString, type);
                        declaredField.set(after, o);
                    }
                }
                if (!declaredField.getType().getName().startsWith("java") && !declaredField.getType().isEnum()) {
                    String name = declaredField.getName();
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
}
