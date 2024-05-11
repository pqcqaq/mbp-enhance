package online.zust.qcqcqc.utils.annotation.convert;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomConvert {
    /**
     * 是否允许为空
     *
     * @return 是否允许为空
     */
    boolean nullable() default true;
}
