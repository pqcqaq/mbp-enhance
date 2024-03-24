package online.zust.qcqcqc.utils.annotation.convert;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FromField {
    /**
     * 原对象中的字段路径
     * 根据路径获取原来对象中的字段值，并赋值给当前字段
     * 如果是多级路径，使用.分隔
     * 如果是List中的元素，使用$开头表示泛型类中的字段（方便开发人员确认）
     *
     * @return 原来对象中的字段路径
     */
    String fieldPath() default "";

    /**
     * 是否允许为空
     *
     * @return 是否允许为空
     */
    boolean nullable() default false;
}
