package online.zust.services.annotation.convent;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FromField {
    /**
     * 原来对象中的字段路径
     * 根据路径获取原来对象中的字段值，并赋值给当前字段
     *
     * @return 原来对象中的字段路径
     */
    String fieldPath() default "";

    /**
     * 是否允许为空
     * @return 是否允许为空
     */
    boolean nullable() default false;
}
