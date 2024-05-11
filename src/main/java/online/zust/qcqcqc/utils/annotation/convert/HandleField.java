package online.zust.qcqcqc.utils.annotation.convert;

import online.zust.qcqcqc.utils.conventer.handler.FieldHandler;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * Date: 2024/3/29
 * Time: 21:02
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HandleField {
    /**
     * 转换器
     *
     * @return 转换器
     */
    Class<? extends FieldHandler> value();
}
