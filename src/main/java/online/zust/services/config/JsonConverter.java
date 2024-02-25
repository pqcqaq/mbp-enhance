package online.zust.services.config;

/**
 * @author qcqcqc
 */
public interface JsonConverter {
    <M, T> T convertValue(M entity, Class<T> clazz);
}
