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
     * 是否在为空时使用默认值
     * @return 是否在为空时使用默认值
     */
    boolean onNull() default false;
}
