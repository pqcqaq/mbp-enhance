package online.zust.qcqcqc.utils.conventer.handler;

import online.zust.qcqcqc.utils.annotation.convert.FromField;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author qcqcqc
 * Date: 2024/3/29
 * Time: 22:43
 */
@Component
public class DefaultFieldHandler implements FieldHandler {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DefaultFieldHandler.class);

    @Override
    public void doConvert(Object rawObject, Object targetObject, Object targetFieldObject, Field targetField) {
        FromField annotation = targetField.getAnnotation(FromField.class);
        String s = annotation.fieldPath();
        Field[] declaredFields = rawObject.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getName().equals(s)) {
                declaredField.setAccessible(true);
                try {
                    targetField.set(targetFieldObject, declaredField.get(rawObject));
                } catch (IllegalAccessException e) {
                    logger.error("字段转换失败: {}", e.getMessage());
                }
            }
        }
    }
}
