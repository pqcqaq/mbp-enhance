package online.zust.qcqcqc.utils.annotation;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * Date: 2025/1/14
 * Time: 下午10:31
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Depth {
    /**
     * 限制递归深度
     * @return 下一级递归深度
     */
    int value() default -1;
}
