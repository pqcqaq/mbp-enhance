package online.zust.qcqcqc.utils.generators.annotation;

import online.zust.qcqcqc.utils.generators.enums.DataType;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * @date 2024/04
 * @time 17-09-59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ColumnType {
    /**
     * 数据类型
     *
     * @return DataType
     */
    DataType type() default DataType.Varchar;

    /**
     * 长度
     *
     * @return int
     */
    int length() default 255;

    /**
     * 是否允许为空
     *
     * @return boolean
     */
    boolean nullable() default true;

    /**
     * 注释
     *
     * @return String
     */
    String comment() default "";
}
