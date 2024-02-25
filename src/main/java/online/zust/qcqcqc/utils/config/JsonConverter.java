package online.zust.qcqcqc.utils.config;

/**
 * @author qcqcqc
 */
public interface JsonConverter {
    <M, T> T convertValue(M entity, Class<T> clazz);
}
