package online.zust.qcqcqc.utils.annotation.convert;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * Date: 2024/3/24
 * Time: 22:01
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireDefault {
    /**
     * 默认值（json）
     * @return 默认值
     */
    String value();

    /**
     * 是否允许为空
     * @return 是否允许为空
     */
    boolean nullable() default true;
}
