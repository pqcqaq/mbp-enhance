package online.zust.qcqcqc.utils.conventer.handler;

import java.lang.reflect.Field;

/**
 * @author qcqcqc
 * Date: 2024/3/29
 * Time: 21:11
 */
@FunctionalInterface
public interface FieldHandler {
    /**
     * 自定义字段转换
     *
     * @param rawObject         原始数据
     * @param targetFieldObject 目标数据
     * @param targetField       目标字段
     *                          targetFieldObject 为目标字段的实例对象，targetField 为目标字段的 Field 对象
     *                          rawObject 为原始数据，rawObject.getClass().getDeclaredFields() 获取原始数据的所有字段
     */
    void doConvert(Object rawObject, Object targetFieldObject, Field targetField);
}
