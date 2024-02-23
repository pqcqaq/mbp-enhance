package online.zust.services.annotation.convent;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SourceField {
    String name() default "";
}
