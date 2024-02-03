package online.zust.services.annotation;

import online.zust.services.EnhanceService;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * 深度搜索注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeepSearch {
    /**
     * 基础id (类字段名)
     *
     * @return 基础id
     */
    String field() default "";

    /**
     * 服务
     *
     * @return 服务
     */
    Class<? extends EnhanceService> service();

    /**
     * 是否允许非空
     *
     * @return 是否允许非空
     */
    boolean notNull() default false;
}
