package online.zust.qcqcqc.utils.annotation.convert;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SourceField {
    /**
     * 原始字段名
     *
     * @return 原始字段名
     */
    String name() default "";

    /**
     * 是否允许为空
     *
     * @return 是否允许为空
     */
    boolean nullable() default false;
}
