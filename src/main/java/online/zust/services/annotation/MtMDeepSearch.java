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
public @interface MtMDeepSearch {
    /**
     * 基础id (表字段名)
     * 关联*表*中表示自己id的字段名
     *
     * @return 基础id
     */
    String baseId();

    /**
     * 目标id（类字段名）
     * 关联*对象*中表示目标id的字段名
     *
     * @return 目标id
     */
    String targetId();

    /**
     * 目标字段（表字段名）
     * @return 目标字段
     */
    String targetField() default "id";

    /**
     * 关联服务
     *
     * @return 关联服务
     */
    Class<? extends EnhanceService> relaService();

    /**
     * 服务
     *
     * @return 服务
     */
    Class<? extends EnhanceService> targetService();

    boolean notNull() default false;
}
