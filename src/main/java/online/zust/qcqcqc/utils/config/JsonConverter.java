package online.zust.qcqcqc.utils.config;

/**
 * @author qcqcqc
 */
public interface JsonConverter {
    /**
     * 将对象转换为另一种对象
     *
     * @param entity 对象
     * @param clazz  对象类型
     * @param <M>    对象类型
     * @param <T>    对象类型
     * @return 对象
     */
    <M, T> T convertValue(M entity, Class<T> clazz);

    /**
     * 从字符串中解析出对象
     *
     * @param entity 字符串
     * @param clazz  对象类型
     * @param <T>    对象类型
     * @return 对象
     */
    <T> T fromString(String entity, Class<T> clazz);
}
