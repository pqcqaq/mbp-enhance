package online.zust.qcqcqc.utils.annotation;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * Date: 2024/4/3
 * Time: 23:16
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LastSqlOnSearch {
    String value() default "";
}
