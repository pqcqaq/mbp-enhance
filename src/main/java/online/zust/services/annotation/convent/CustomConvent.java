package online.zust.services.annotation.convent;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomConvent {
    boolean nullable() default true;
}
