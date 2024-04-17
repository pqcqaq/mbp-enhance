package online.zust.qcqcqc.utils.annotation;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 23:57
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MsgOnCheckError {
    String value() default "";
}
